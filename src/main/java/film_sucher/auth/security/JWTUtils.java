package film_sucher.auth.security;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import film_sucher.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtils{
    private final JWTProps jwtProps;
    public JWTUtils(JWTProps jwtProps){
        this.jwtProps = jwtProps;
    }

    public String generateToken(String username, Long id, User.Role role){
        //current date
        Date now = new Date();
        //end date
        Date expiry = new Date(now.getTime() + jwtProps.getValidity());
        // key
        Key key = Keys.hmacShaKeyFor(jwtProps.getSecret().getBytes());
        // roles
        List<String> roles = List.of("ROLE_" + role);

        String token = Jwts.builder()
                    .setSubject(username)
                    .claim("roles", roles)
                    .claim("id", id)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        
        return token;
    }

    public Claims parseToken(String token) throws JwtException{
        Key key = Keys.hmacShaKeyFor(jwtProps.getSecret().getBytes());
        return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }
}