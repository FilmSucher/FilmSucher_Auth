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
import film_sucher.auth.dto.UserRequest;
import film_sucher.auth.dto.UserResponse;
import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.service.AdminService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    public AdminService service;

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
        doNothing().when(service).addUser(user);
        ResponseEntity<?> result = controller.addUser(user);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("User successfully created", result.getBody());
    }

    @Test
    public void getDBErrorAdd(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).addUser(user);
        ResponseEntity<?> result = controller.addUser(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error access in user-DB", result.getBody());
    }

    @Test
    public void getUnexpectedAdd(){
        doThrow(new RuntimeException("Error!")).when(service).addUser(user);
        ResponseEntity<?> result = controller.addUser(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }
    // --------------------------------
    // change
    @Test
    public void getSuccessfullChange(){
        doNothing().when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User successfully changed", result.getBody());
    }

    @Test
    public void getNotFoundChange(){
        doThrow(new EntityNotFoundException("NotFound in DB", new RuntimeException())).when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("NotFound in DB", result.getBody());
    }

    @Test
    public void getDBErrorChange(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error access in user-DB", result.getBody());
    }

    @Test
    public void getUnexpectedChange(){
        doThrow(new RuntimeException("Error!")).when(service).changeUser(userId, request);
        ResponseEntity<?> result = controller.changeUser(request, userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }

    // delete
    @Test
    public void getSuccessfullDel(){
        doNothing().when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEquals("User successfully deleted", result.getBody());
    }

    @Test
    public void getNotFoundDel(){
        doThrow(new EntityNotFoundException("NotFound in DB", new RuntimeException())).when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("NotFound in DB", result.getBody());
    }

    @Test
    public void getDBErrorDel(){
        doThrow(new DatabaseException("DB Error", new RuntimeException())).when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error access in user-DB", result.getBody());
    }

    @Test
    public void getUnexpectedDel(){
        doThrow(new RuntimeException("Error!")).when(service).deleteUser(userId);
        ResponseEntity<?> result = controller.deleteUser(userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
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
        assertEquals("Error access in user-DB", result.getBody());
    }

    @Test
    public void getUnexpectedGetAll(){
        when(service.getAll()).thenThrow(new RuntimeException("Error"));
        ResponseEntity<?> result = controller.getAllUsers();
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
    }
}
