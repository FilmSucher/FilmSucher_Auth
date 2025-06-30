package film_sucher.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import film_sucher.auth.entity.User;
import film_sucher.auth.repository.AuthRepo;
import film_sucher.auth.security.JWTUtils;

@Service
public class AuthService{

    private final AuthRepo repo;
    private final JWTUtils jwtUtils;


    @Autowired
    public AuthService(AuthRepo repo, JWTUtils jwtUtils){
        this.repo = repo;
        this.jwtUtils = jwtUtils;
    }

    public Optional<String> authenticate (String username, String password){
        // get user or null
        Optional<User> user = repo.findByUsername(username);
        // if not null and password is right
        if(user.isPresent()){
            if (passMatches(password, user.get().getPassword())){
                // make new Token
                String token = jwtUtils.generateToken(username, user.get().getRole());
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }

    // check password matches 
    private boolean passMatches(String raw, String hashed){
        return new BCryptPasswordEncoder().matches(raw, hashed);
    }

}