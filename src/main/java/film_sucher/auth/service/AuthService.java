package film_sucher.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.exceptions.UnauthorizedException;
import film_sucher.auth.repository.AuthRepo;
import film_sucher.auth.security.JWTUtils;
import jakarta.transaction.Transactional;

@Service
public class AuthService{

    private final AuthRepo repo;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder encoder;


    @Autowired
    public AuthService(AuthRepo repo, JWTUtils jwtUtils, PasswordEncoder encoder){
        this.repo = repo;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
    }

    public String authenticate (String username, String password){
        Optional<User> user;
        try {
            // get user or null
            user = repo.findByUsername(username);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error receiving user from DB", e);
        }

        if (user.isEmpty()) throw new UnauthorizedException("User not found");
        if (encoder.matches(password, user.get().getPassword())){
            // make and return new Token
            return jwtUtils.generateToken(username, user.get().getId(), user.get().getRole());
        } else {
            throw new UnauthorizedException("Password is wrong");
        }
    }

    @Transactional
    public void register (String username, String password){
        String hashPassword = encoder.encode(password);
        User newUser = new User(username, hashPassword, User.Role.USER);
        try {
            repo.save(newUser);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error register user in DB", e);
        }
    }
}