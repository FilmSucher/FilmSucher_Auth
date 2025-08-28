package film_sucher.auth.unit.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import film_sucher.auth.dto.UserRequest;
import film_sucher.auth.dto.UserResponse;
import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.repository.AuthRepo;
import film_sucher.auth.service.AdminService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    private final Long userId = 1L;
    private final String username = "username";
    private final String password = "password";
    private final String hashPassword = new BCryptPasswordEncoder().encode(password);
    private final User.Role role = User.Role.USER;
    private final User user = new User(username, password, role);
    private final User userDB = new User(username, hashPassword, role);
    private final UserRequest request = new UserRequest(username, null, role);


    @Mock
    private AuthRepo repo;
    @Mock
    private PasswordEncoder encoder; 

    @InjectMocks
    private AdminService service;

    // add
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullAdd(){
        when(encoder.encode(password)).thenReturn(hashPassword);
        service.addUser(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());

        User saved = captor.getValue();
        assertEquals(username, saved.getUsername());
        assertEquals(hashPassword, saved.getPassword());
        assertEquals(role, saved.getRole());
    }

    @Test
    public void getDBErrorAdd(){
        when(encoder.encode(password)).thenReturn(hashPassword);
        doThrow(new DataAccessException("Error") {}).when(repo).save(any(User.class));

        DatabaseException ex = assertThrows(DatabaseException.class, () -> 
            service.addUser(user));

        assertEquals("Error register user in DB", ex.getMessage());
    }

    // change
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullChange(){
        when(repo.existsById(userId)).thenReturn(true);
        when(repo.findById(userId)).thenReturn(Optional.of(userDB));
        service.changeUser(userId, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());

        User saved = captor.getValue();
        assertEquals(username, saved.getUsername());
        assertEquals(hashPassword, saved.getPassword());
        assertEquals(role, saved.getRole());
    }

    @Test
    public void getNotFoundErrorChange(){
        when(repo.existsById(userId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> 
            service.changeUser(userId, request));

        assertEquals("Error! User with id: " + userId + " not found", ex.getMessage());

        verify(repo, never()).save(any());
    }

    @Test
    public void getDBErrorChange(){
        when(repo.existsById(userId)).thenReturn(true);
        when(repo.findById(userId)).thenReturn(Optional.of(userDB));
    
        doThrow(new DataAccessException("Error") {}).when(repo).save(any(User.class));

        DatabaseException ex = assertThrows(DatabaseException.class, () -> 
            service.changeUser(userId, request));

        assertEquals("Error changing user in DB", ex.getMessage());
    }

    // delete
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullDel(){
        when(repo.existsById(userId)).thenReturn(true);
        service.deleteUser(userId);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(repo).deleteById(captor.capture());

        Long savedId = captor.getValue();
        assertEquals(userId, savedId);
    }

    @Test
    public void getNotFoundErrorDel(){
        when(repo.existsById(userId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> 
            service.deleteUser(userId));

        assertEquals("Error! User with id: " + userId + " not found", ex.getMessage());
        verify(repo, never()).deleteById(any());
    }

    @Test
    public void getDBErrorDel(){
        when(repo.existsById(userId)).thenReturn(true);
        doThrow(new DataAccessException("Error") {}).when(repo).deleteById(any(Long.class));

        DatabaseException ex = assertThrows(DatabaseException.class, () -> 
            service.deleteUser(userId));
        assertEquals("Error deleting user in DB", ex.getMessage());
    }

    // get user
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullGet(){
        User BDUser = new User(user.getUsername(), user.getPassword(), user.getRole());
        BDUser.setId(userId);

        when(repo.findById(userId)).thenReturn(Optional.of(BDUser));
        UserResponse resultUsers = service.getUser(userId);

        assertEquals(userId, resultUsers.getId());
        assertEquals(username, resultUsers.getUsername());
        assertEquals(role, resultUsers.getRole());
    }

    @Test
    public void getDBErrorGet(){
        doThrow(new DataAccessException("Error") {}).when(repo).findById(userId);

        DatabaseException ex = assertThrows(DatabaseException.class, () -> 
            service.getUser(userId));
        assertEquals("Error receiving users in DB", ex.getMessage());
    }

    // get all
    // ---------------------------------------------------------------------
    @Test
    public void getSuccessfullGetAll(){
        User BDUser = new User(user.getUsername(), user.getPassword(), user.getRole());
        BDUser.setId(userId);

        when(repo.findAll()).thenReturn(List.of(BDUser));
        List<UserResponse> resultUsers = service.getAll();

        assertEquals(1, resultUsers.size());
        assertEquals(userId, resultUsers.get(0).getId());
        assertEquals(username, resultUsers.get(0).getUsername());
        assertEquals(role, resultUsers.get(0).getRole());
    }

    @Test
    public void getDBErrorGetAll(){
        doThrow(new DataAccessException("Error") {}).when(repo).findAll();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> 
            service.getAll());
        assertEquals("Error receiving users in DB", ex.getMessage());
    }
}
