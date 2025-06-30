package film_sucher.auth.security;

import java.util.Date;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import film_sucher.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
//get data from application.properties
@ConfigurationProperties(prefix="token")
public class JWTUtils{

    private String secret = "";
    private long validity = 0;

    public String generateToken(String username, User.Role role){
        //current date
        Date now = new Date();
        //end date
        Date expiry = new Date(now.getTime() + validity);

        String token = Jwts.builder()
                    .setSubject(username)
                    .claim("role", role)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                    .compact();
        
        return token;
    }
}