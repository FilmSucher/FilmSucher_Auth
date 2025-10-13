package film_sucher.auth.unit.controller;

import java.util.List;

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

import film_sucher.auth.controller.AdminController;
import film_sucher.auth.dto.ApiResponseDTO;
import film_sucher.auth.dto.UserRequest;
import film_sucher.auth.dto.UserResponse;
import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.security.JWTUtils;
import film_sucher.auth.service.AdminService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    public AdminService service;
    @Mock
    public JWTUtils jwtUtils;

    @InjectMocks
    public AdminController controller;

    private final Long userId = 1L;
    private final String username = "user";
    private final String password = "password";
    private final User.Role role = User.Role.USER;
    private final User user = new User(username, password, role); 
    private final UserResponse response = new UserResponse(userId, username, role);
    private final UserRequest request = new UserRequest(username, password, role);


    // add
    @Test
    public void getSuccessfullAdd(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doNothing().when(service).addUser(user);
        ResponseEntity<?> result = controller.addUser(user);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("User successfully created", body.getMessage());
        assertEquals(null, body.getE());
        assertEquals(HttpStatus.CREATED, body.getStatus());
    }

    @Test
    public void getDBErrorAdd(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).addUser(user);
        ResponseEntity<?> result = controller.addUser(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error access in user-DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedAdd(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new RuntimeException("Error!")).when(service).addUser(user);
        ResponseEntity<?> result = controller.addUser(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof RuntimeException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }
    // --------------------------------
    // change
    @Test
    public void getSuccessfullChange(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doNothing().when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("User successfully changed", body.getMessage());
        assertEquals(null, body.getE());
        assertEquals(HttpStatus.OK, body.getStatus());
    }

    @Test
    public void getNotFoundChange(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new EntityNotFoundException("NotFound in DB", new RuntimeException())).when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals(body.getE().getMessage(), body.getMessage());
        assertTrue(body.getE() instanceof EntityNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, body.getStatus());
    }

    @Test
    public void getDBErrorChange(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error access in user-DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedChange(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new RuntimeException("Error!")).when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof RuntimeException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    // delete
    @Test
    public void getSuccessfullDel(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doNothing().when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("User successfully deleted", body.getMessage());
        assertEquals(null, body.getE());
        assertEquals(HttpStatus.NO_CONTENT, body.getStatus());
    }

    @Test
    public void getNotFoundDel(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new EntityNotFoundException("NotFound in DB", new RuntimeException())).when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals(body.getE().getMessage(), body.getMessage());
        assertTrue(body.getE() instanceof EntityNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, body.getStatus());
    }

    @Test
    public void getDBErrorDel(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error access in user-DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedDel(){
        when(jwtUtils.getUserFromToken()).thenReturn(response);
        doThrow(new RuntimeException("Error!")).when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    // get
    @Test
    public void getSuccessfullGet(){
        when(service.getUser(userId)).thenReturn(response);
        ResponseEntity<?> result = controller.getUser(userId);
    
        assertEquals(response, result.getBody());
    }

    @Test
    public void getDBErrorGet(){
        when(service.getAll()).thenThrow(new DatabaseException("DB Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getAllUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error access in user-DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedGet(){
        when(service.getAll()).thenThrow(new RuntimeException("Error"));
        ResponseEntity<?> result = controller.getAllUsers();
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    // get all
    @Test
    public void getSuccessfullGetAll(){
        when(service.getAll()).thenReturn(List.of(response));
        ResponseEntity<?> result = controller.getAllUsers();
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List<?>);
        
        List<?> body = (List<?>) result.getBody();
        assertEquals(response, body.get(0));
    }

    @Test
    public void getDBErrorGetAll(){
        when(service.getAll()).thenThrow(new DatabaseException("DB Error", new RuntimeException()));
        ResponseEntity<?> result = controller.getAllUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Error access in user-DB", body.getMessage());
        assertTrue(body.getE() instanceof DatabaseException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }

    @Test
    public void getUnexpectedGetAll(){
        when(service.getAll()).thenThrow(new RuntimeException("Error"));
        ResponseEntity<?> result = controller.getAllUsers();
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody() instanceof ApiResponseDTO);

        ApiResponseDTO body = (ApiResponseDTO) result.getBody();
        assertEquals("Unexpected error", body.getMessage());
        assertTrue(body.getE() instanceof Exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, body.getStatus());
    }
}
