package film_sucher.auth.unit;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import film_sucher.auth.entity.User;
import film_sucher.auth.repository.AuthRepo;
import film_sucher.auth.security.JWTUtils;
import film_sucher.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class ServiceTest{

    //hash-password for "password123"
    public User user = new User("username", new BCryptPasswordEncoder().encode("password123"), User.Role.USER);
    
    @Mock
    public AuthRepo repo;
    @Mock
    public JWTUtils jwtUtils;

    @InjectMocks
    public AuthService service;

    @Test
    public void getToken(){
        when(repo.findByUsername("username")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken("username", 1L, User.Role.USER)).thenReturn("TOKEN");

        assertEquals(Optional.of("TOKEN"),service.authenticate("username", "password123"));
    }

    @Test
    public void getNull(){
        when(repo.findByUsername("username")).thenReturn(Optional.empty());

        assertEquals(Optional.empty(),service.authenticate("username", "password123"));
    }   
}