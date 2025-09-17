package film_sucher.auth.security;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import film_sucher.auth.dto.UserResponse;
import film_sucher.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtils{
    private final JWTProps jwtProps;

    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);

    public JWTUtils(JWTProps jwtProps){
        this.jwtProps = jwtProps;
    }

    public String generateToken(String username, Long id, User.Role role){
        //current date
        Date now = new Date();
        logger.info("Getted currently date");
        //end date
        Date expiry = new Date(now.getTime() + jwtProps.getValidity());
        logger.info("Maked expiry date");
        // key
        Key key = Keys.hmacShaKeyFor(jwtProps.getSecret().getBytes());
        logger.info("Generated key");
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

        logger.info("Token buildet");
        return token;
    }

    public Claims parseToken(String token) throws JwtException{
        Key key = Keys.hmacShaKeyFor(jwtProps.getSecret().getBytes());
        logger.info("Generated key");

        Claims parserToken = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        logger.info("Generated claims");
        return parserToken;
    }

    public UserResponse getUserFromToken(){
        // name and id
        Map<String, Object> principal = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Getted principals");
        Long id = ((Integer) principal.get("id")).longValue();
        String username = ((String) principal.get("username"));
        logger.info("Getted id and username");

        // role
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        logger.info("Getted authorities");
        String roleString = (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) ? "ADMIN" : "USER";
        logger.info("Getted role");
        
        return new UserResponse(id, username, User.Role.valueOf(roleString));
    }
}