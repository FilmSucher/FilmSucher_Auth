package film_sucher.auth.unit;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import film_sucher.auth.controller.AuthController;
import film_sucher.auth.dto.UserDataRequest;
import film_sucher.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class ControllerTest{

    private final UserDataRequest loginRequest = new UserDataRequest("","");

    @Mock
    public AuthService service;

    @InjectMocks
    public AuthController controller;

    @Test
    public void getResponceToken(){
        when(service.authenticate("","")).thenReturn(Optional.of("token"));

        assertEquals(ResponseEntity.ok("token"), controller.login(loginRequest));
    }

    @Test
    public void getResponceNull(){
        when(service.authenticate("","")).thenReturn(Optional.empty());

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), controller.login(loginRequest));
    }
}