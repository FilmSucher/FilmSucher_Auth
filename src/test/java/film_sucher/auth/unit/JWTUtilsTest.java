package film_sucher.auth.unit;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import film_sucher.auth.entity.User;
import film_sucher.auth.security.JWTProps;
import film_sucher.auth.security.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JWTUtilsTest{

    private JWTProps jwtProps;
    private JWTUtils jwt;

    @BeforeEach
    public void setUp(){
        jwtProps = new JWTProps(){
            @Override
            public String getSecret() {
                return "my-test-secret-key-my-test-secret-key";
            }
            @Override
            public Long getValidity() {
                return 1000L*60*60;
            }
        };

        jwt = new JWTUtils(jwtProps);
    }

    @Test
    public void getSuccessfullGen(){
        String token = jwt.generateToken("username", 1L, User.Role.USER);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Key key = Keys.hmacShaKeyFor(jwtProps.getSecret().getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("username", claims.getSubject());
        assertEquals(1, claims.get("id", Integer.class));
        assertEquals(List.of("ROLE_USER"), claims.get("roles",List.class));
    }

    @Test
    public void getSuccessfullParse(){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProps.getValidity());
        Key key = Keys.hmacShaKeyFor(jwtProps.getSecret().getBytes());
        List<String> roles = List.of("ROLE_" + User.Role.USER);

        String token = Jwts.builder()
                    .setSubject("username")
                    .claim("roles", roles)
                    .claim("id", 1L)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        
        Claims claims = jwt.parseToken(token);

        assertEquals("username", claims.getSubject());
        assertEquals(1, claims.get("id", Integer.class));
        assertEquals(List.of("ROLE_USER"), claims.get("roles",List.class));
    }

    @Test
    public void getInvalidSignatureErrorParse(){
        String token = jwt.generateToken("username", 1L, User.Role.USER);

        JWTProps wrongProps = new JWTProps(){
            @Override
            public String getSecret() {
                return "other-secret-other-secret-other-secret";
            }
            @Override
            public Long getValidity() {
                return 1000L*60*60;
            }
        };
        JWTUtils badUtils = new JWTUtils(wrongProps);

        assertThrows(JwtException.class, () -> badUtils.parseToken(token));
    }

    @Test
    public void getExpiredErrorParse(){
        JWTProps shortExpiry = new JWTProps(){
            @Override
            public String getSecret() {
                return "my-test-secret-key-my-test-secret-key";
            }
            @Override
            public Long getValidity() {
                return 5L;
            }
        };

        JWTUtils shortLived = new JWTUtils(shortExpiry);
        String token = shortLived.generateToken("username", 1L, User.Role.USER);

        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {
        }

        assertThrows(ExpiredJwtException.class, () -> shortLived.parseToken(token));
    }
}