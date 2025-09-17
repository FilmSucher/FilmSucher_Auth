package film_sucher.auth.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import film_sucher.auth.dto.ApiResponseDTO;
import film_sucher.auth.dto.UserRequest;
import film_sucher.auth.dto.UserResponse;
import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.security.JWTUtils;
import film_sucher.auth.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Controller", 
    description = "Manage users in the database. Access only for authorized administrators")
public class AdminController {
    private final AdminService service;
    private final JWTUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(AdminService service, JWTUtils jwtUtils){
        this.service = service;
        this.jwtUtils = jwtUtils;
    }

    // add
    @Operation(summary = "Add User", description = "Adding a user to the database. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="201", description="User successfully added"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody User newUser){
        UserResponse admin = jwtUtils.getUserFromToken();
        try {
            logger.info("Attempt to add user with username:{} from admin: {} with userId: {}", newUser.getUsername(), admin.getUsername(), admin.getId());
            service.addUser(newUser);
            logger.info("User with username {} succesfully added in DB", newUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO("User successfully created", null, HttpStatus.CREATED));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while created user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error access in user-DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while created user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // change
    @Operation(summary = "Modifie User", description = "Changing a user in the database. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="User successfully modified"),
        @ApiResponse(responseCode="404", description="User is missing"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PatchMapping("/user/{id}")
    public ResponseEntity<?> changeUser(@RequestBody UserRequest changedUser, @PathVariable("id") Long userId){
        UserResponse admin = jwtUtils.getUserFromToken();
        try {
            logger.info("Attempt to change user with id: {} from admin: {} with userId: {}", userId, admin.getUsername(), admin.getId());
            service.changeUser(userId, changedUser);
            logger.info("User with userId {} succesfully changed in DB", userId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("User successfully changed", null, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            logger.warn("User with ID: {} not found", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND ).body(new ApiResponseDTO(e.getMessage(), e, HttpStatus.NOT_FOUND));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while changed user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error access in user-DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while changed user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // delete
    @Operation(summary = "Remove User", description = "Removing a user from the database. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="204", description="User successfully deleted"),
        @ApiResponse(responseCode="404", description="User is missing"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId){
        UserResponse admin = jwtUtils.getUserFromToken();
        try {
            logger.info("Attempt to delete user with id: {} from admin: {} with userId: {}", userId, admin.getUsername(), admin.getId());
            service.deleteUser(userId);
            logger.info("User with userId {} succesfully deleted from DB", userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponseDTO("User successfully deleted", null, HttpStatus.NO_CONTENT));
        } catch (EntityNotFoundException e) {
            logger.warn("User with ID: {} not found", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND ).body(new ApiResponseDTO(e.getMessage(), e, HttpStatus.NOT_FOUND));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while deleting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error access in user-DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while deleting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // get by id
    @Operation(summary = "Get User", description = "Getting a user by id from the database. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="User successfully received"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long userId){
        UserResponse user;
        try {
            user = service.getUser(userId);
            logger.info("User with userId: {} getted from DB", userId);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while getting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error access in user-DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while getting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // get all
    @Operation(summary = "Get List Users", description = "Getting a list of all users from the database. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="List of users successfully received"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @GetMapping("/user")
    public ResponseEntity<?> getAllUsers(){
        List<UserResponse> users;
        try {
            users = service.getAll();
            logger.info("List of users getted from DB");
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while getting list of alle users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error access in user-DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while getting list of alle users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
