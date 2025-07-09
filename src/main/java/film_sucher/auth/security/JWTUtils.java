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

    private String secret = "super_duper_secret_testkey_filler";
    private long validity = 3600;

    //default construktor
    public JWTUtils(){}
    //test construktor
    public JWTUtils(String secret, long validity){
        this.secret = secret;
        this.validity = validity;
    }

    public String generateToken(String username, Long id, User.Role role){
        //current date
        Date now = new Date();
        //end date
        Date expiry = new Date(now.getTime() + validity);

        String token = Jwts.builder()
                    .setSubject(username)
                    .claim("role", role)
                    .claim("id", id)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                    .compact();
        
        return token;
    }
}