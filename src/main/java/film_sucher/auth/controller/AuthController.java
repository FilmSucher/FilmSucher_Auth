package film_sucher.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import film_sucher.auth.dto.ApiResponseDTO;
import film_sucher.auth.dto.TokenResponse;
import film_sucher.auth.dto.UserDataRequest;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.exceptions.UnauthorizedException;
import film_sucher.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", 
    description = "Controller for user authentication and registration")

public class AuthController {
    private final AuthService service;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService service){
        this.service = service;
    }

    // login
    @Operation(summary = "User login", description = "Checking the username and password of the user in the DB. If successful, generating a token")
    @ApiResponses(value = {
        @ApiResponse(responseCode="200", description="Data is correct. Token is returned"),
        @ApiResponse(responseCode="401", description="Data (name or password) is incorrect"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDataRequest request){
        String token; 
        try {
            token = service.authenticate(request.getUsername(), request.getPassword());

            logger.info("Token successfully generated");
            return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(token));
        } catch (UnauthorizedException e) {
            logger.warn("UnauthorizedException while logging: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO(e.getMessage(), e, HttpStatus.UNAUTHORIZED));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while logging: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error access in user-DB", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while logging: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // register
    @Operation(summary = "Registration", description = "Registration of new users. Username and password as arguments, role is always USER")
    @ApiResponses(value = {
        @ApiResponse(responseCode="201", description="User successfully created"),
        @ApiResponse(responseCode="500", description="Error on backend side")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDataRequest request){
        try{
            service.register(request.getUsername(), request.getPassword());

            logger.info("User successfully registred");
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO("User successfully created" , null, HttpStatus.CREATED));
        } catch (DatabaseException e) {
            logger.warn("DatabaseException while registring: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(new ApiResponseDTO("Error access in user-DB" , e, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.warn("UnexpectedException while registring: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Unexpected error", e, HttpStatus.INTERNAL_SERVER_ERROR));
        } 

    }
}
