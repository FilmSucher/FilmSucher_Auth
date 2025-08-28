package film_sucher.auth.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import film_sucher.auth.controller.AuthController;
import film_sucher.auth.dto.ApiResponseDTO;
import film_sucher.auth.dto.TokenResponse;
import film_sucher.auth.dto.UserDataRequest;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.exceptions.UnauthorizedException;
import film_sucher.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest{

    private final String token = "mocked-jwt-token";
    private final String username = "test";
    private final String password = "test";
    private final UserDataRequest loginRequest = new UserDataRequest(username, password);

    @Mock
    public AuthService service;

    @InjectMocks
    public AuthController controller;

    // login tests
    @Test
    public void getResponceTokenLog(){
        when(service.authenticate(username,password)).thenReturn(token);

        ResponseEntity<?> response = controller.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof TokenResponse);
        assertEquals(token, ((TokenResponse) response.getBody()).getToken());
    }
    @Test
    public void getUnauthorizedLog(){
        when(service.authenticate(username,password)).thenThrow(new UnauthorizedException("User not found"));

        ResponseEntity<?> response = controller.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) response.getBody();
        assertEquals(body.getE().getMessage(), body.getMessage());
        assertTrue(body.getE() instanceof UnauthorizedException);
        assertEquals(HttpStatus.UNAUTHORIZED, body.getStatus());
    }
    @Test
    public void getDBErrorLog(){
        when(service.authenticate(username,password)).thenThrow(new DatabaseException("DB Error", new RuntimeException()));

        ResponseEntity<?> response = controller.login(loginRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) response.getBody();
        assertEquals("Error access in user-DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }
    @Test
    public void getUnknownErrorLog(){
        when(service.authenticate(username,password)).thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<?> response = controller.login(loginRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) response.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof RuntimeException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    // register test
    @Test
    public void getSuccessfullReg(){
        doNothing().when(service).register(username, password);
        ResponseEntity<?> response = controller.register(loginRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) response.getBody();
        assertEquals("User successfully created", body.getMessage());
        assertEquals(null, body.getE());
        assertEquals(HttpStatus.CREATED, body.getStatus());
    }

    @Test
    public void getDBErrorReg(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).register(username,password);
        ResponseEntity<?> response = controller.register(loginRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) response.getBody();
        assertEquals("Error access in user-DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }
    @Test
    public void getUnknownErrorReg(){
        doThrow(new RuntimeException("Unexpected")).when(service).register(username,password);
        ResponseEntity<?> response = controller.register(loginRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) response.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof RuntimeException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }
}