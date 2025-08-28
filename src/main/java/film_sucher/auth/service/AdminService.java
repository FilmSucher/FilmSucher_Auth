package film_sucher.auth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import film_sucher.auth.dto.UserRequest;
import film_sucher.auth.dto.UserResponse;
import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.repository.AuthRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AdminService {
    private final AuthRepo repo;
    private final PasswordEncoder encoder;

    public AdminService(AuthRepo repo, PasswordEncoder encoder){
        this.repo = repo;
        this.encoder = encoder;
    }

    // addUser
    @Transactional
    public void addUser(User newUser){
        String hashPassword = encoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        try {
            repo.save(newUser);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error register user in DB", e);
        }
    }
    // changeUser
    @Transactional
    public void changeUser(Long userId, UserRequest request){
        try {
            User changedUser;
            if(repo.existsById(userId)) {
                changedUser = repo.findById(userId).get();
                if(request.getUsername() != null) changedUser.setUsername(request.getUsername());
                if(request.getRole() != null) changedUser.setRole(request.getRole());
                if(request.getPassword() != null && !request.getPassword().isBlank()) changedUser.setPassword(encoder.encode(request.getPassword()));
            } else{
                throw new EntityNotFoundException("Error! User with id: " + userId + " not found");
            }
            repo.save(changedUser);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error changing user in DB", e);
        }
    }

    // deleteUser
    @Transactional
    public void deleteUser(Long userId){
        try {
            if(!repo.existsById(userId)) throw new EntityNotFoundException("Error! User with id: " + userId + " not found");
            repo.deleteById(userId);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error deleting user in DB", e);
        }
    }

    //get
    public UserResponse getUser(Long userId){
        User user;
        try {
            user = repo.findById(userId).get();
        } catch (DataAccessException e) {
            throw new DatabaseException("Error receiving users in DB", e);
        }
        UserResponse dtoUser = new UserResponse(user.getId(), user.getUsername(), user.getRole());
        return dtoUser;
    }

    //get list
    public List<UserResponse> getAll(){
        List<User> users;
        try {
            users = (List<User>) repo.findAll();
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new DatabaseException("Error receiving users in DB", e);
        }

        List<UserResponse> dtoUsers = new ArrayList<>();
        for (User user : users) {
            dtoUsers.add(new UserResponse(user.getId(), user.getUsername(), user.getRole()));
        }
        return dtoUsers;
    }
}
