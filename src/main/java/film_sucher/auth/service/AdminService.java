package film_sucher.auth.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(AuthRepo repo, PasswordEncoder encoder){
        this.repo = repo;
        this.encoder = encoder;
    }

    // addUser
    @Transactional
    public void addUser(User newUser){
        String hashPassword = encoder.encode(newUser.getPassword());
        logger.info("Password for new user encoded");
        newUser.setPassword(hashPassword);
        try {
            repo.save(newUser);
            logger.info("New user successfully saved");
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
                logger.info("Changed user successfully finded");
                changedUser = repo.findById(userId).get();
                logger.info("Changed user successfully getted");
                if(request.getUsername() != null) changedUser.setUsername(request.getUsername());
                if(request.getRole() != null) changedUser.setRole(request.getRole());
                if(request.getPassword() != null && !request.getPassword().isBlank()) changedUser.setPassword(encoder.encode(request.getPassword()));
                logger.info("Changed user successfully changed");
            } else{
                throw new EntityNotFoundException("Error! User with id: " + userId + " not found");
            }
            repo.save(changedUser);
            logger.info("Changed user successfully saved");
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
            logger.info("User successfully deleted");
        } catch (DataAccessException e) {
            throw new DatabaseException("Error deleting user in DB", e);
        }
    }

    //get
    public UserResponse getUser(Long userId){
        User user;
        try {
            user = repo.findById(userId).get();
            logger.info("User successfully getted");
        } catch (DataAccessException e) {
            throw new DatabaseException("Error receiving users in DB", e);
        }
        UserResponse dtoUser = new UserResponse(user.getId(), user.getUsername(), user.getRole());
        logger.info("UserResponse successfully maked from User");
        return dtoUser;
    }

    //get list
    public List<UserResponse> getAll(){
        List<User> users;
        try {
            users = (List<User>) repo.findAll();
            logger.info("List of users successfully getted");
        } catch (DataAccessException e) {
            throw new DatabaseException("Error receiving users in DB", e);
        }

        List<UserResponse> dtoUsers = new ArrayList<>();
        for (User user : users) {
            dtoUsers.add(new UserResponse(user.getId(), user.getUsername(), user.getRole()));
        }
        logger.info("List of UserResponses successfully maked from List of Users");
        return dtoUsers;
    }
}
