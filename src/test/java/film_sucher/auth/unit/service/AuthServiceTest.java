package film_sucher.auth.unit.service;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import film_sucher.auth.entity.User;
import film_sucher.auth.exceptions.DatabaseException;
import film_sucher.auth.exceptions.UnauthorizedException;
import film_sucher.auth.repository.AuthRepo;
import film_sucher.auth.security.JWTUtils;
import film_sucher.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest{

    public Long userId = 1L;
    public String username = "username";
    public String password = "password";
    public String hashPassword = new BCryptPasswordEncoder().encode(password);
    public User.Role role = User.Role.USER;
    public User user = new User(username, hashPassword, role);
    
    @Mock
    public AuthRepo repo;
    @Mock
    public JWTUtils jwtUtils;
    @Mock
    public PasswordEncoder encoder;

    @InjectMocks
    public AuthService service;

    // authenticate
    // ------------------------------------------------------------------------------
    @Test
    public void getTokenAuth(){
        user.setId(userId);
        when(repo.findByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches(password, hashPassword)).thenReturn(true);
        when(jwtUtils.generateToken(username, userId, role)).thenReturn("TOKEN");

        assertEquals("TOKEN", service.authenticate(username, password));
    }

    @Test 
    public void getDBErrorAuth(){
        when(repo.findByUsername(username)).thenThrow(new DataAccessException("DBError"){});

        DatabaseException ex = assertThrows(DatabaseException.class, () -> 
            service.authenticate(username, password));

        assertEquals("Error receiving user from DB", ex.getMessage());

    }
    @Test 
    public void getUserNotFoundAuth(){
        when(repo.findByUsername(username)).thenReturn(Optional.empty());
        
        UnauthorizedException  ex = assertThrows(UnauthorizedException.class, () -> 
            service.authenticate(username, password));

        assertEquals("User not found", ex.getMessage());

    }
    @Test 
    public void getWrongPasswordAuth(){
        when(repo.findByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches("wrongpassword", hashPassword)).thenReturn(false);

         UnauthorizedException  ex = assertThrows(UnauthorizedException.class, () -> 
            service.authenticate(username, "wrongpassword"));

        assertEquals("Password is wrong", ex.getMessage());
    }

    // register
    // ------------------------------------------------------------------------------
    @Test
    public void getSuccesfullReg(){
        when(encoder.encode(password)).thenReturn(hashPassword);

        service.register(username, password);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());

        User saved = captor.getValue();
        assertEquals(username, saved.getUsername());
        assertEquals(hashPassword, saved.getPassword());
        assertEquals(role, saved.getRole());
    }

    @Test
    public void getDBErrorReg(){
        when(encoder.encode(password)).thenReturn(hashPassword);
        doThrow(new DataAccessException("Error") {}).when(repo).save(any(User.class));

        DatabaseException ex = assertThrows(DatabaseException.class, () -> 
            service.register(username, password));

        assertEquals("Error register user in DB", ex.getMessage());
    }
}