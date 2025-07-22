package film_sucher.auth.controller;

import java.util.List;

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

import film_sucher.auth.dto.UserRequest;
import film_sucher.auth.dto.UserResponse;
import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
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

    public AdminController(AdminService service){
        this.service = service;
    }

    // add
    @Operation(summary = "Add User", description = "Adding a user to the database. For admins only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode="201", description="User successfully added"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody User newUser){
        try {
            service.addUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User successfully created");
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body("Error access in user-DB");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
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
    public ResponseEntity<?> changeUser(@RequestBody UserRequest changedUser, @PathVariable Long userId){
        try {
            service.changeUser(userId, changedUser);
            return ResponseEntity.status(HttpStatus.OK).body("User successfully changed");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND ).body(e.getMessage());
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body("Error access in user-DB");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
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
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        try {
            service.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User successfully deleted");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND ).body(e.getMessage());
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body("Error access in user-DB");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
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
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body("Error access in user-DB");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
}
