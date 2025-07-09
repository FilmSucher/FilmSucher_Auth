package film_sucher.auth.unit;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import film_sucher.auth.PropsReaderUtil;
import film_sucher.auth.entity.User;
import film_sucher.auth.security.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
public class JWTUtilsTest{

    private JWTUtils jwt;
    private String secret;
    private long validity;

    @BeforeEach
    public void setUp(){
        Properties props = PropsReaderUtil.load();
        secret = props.getProperty("token.secret");
        validity = Long.parseLong(props.getProperty("token.validity"));

        jwt = new JWTUtils(secret, validity);
    }

    @Test
    public void getToken(){
        String token = jwt.generateToken("username", 1L, User.Role.USER);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("username", claims.getSubject());
        assertEquals("USER", claims.get("role"));
    }
}