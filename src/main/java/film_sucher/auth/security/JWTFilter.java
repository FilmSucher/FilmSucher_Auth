package film_sucher.auth.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter{

    private final JWTUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    public JWTFilter(JWTUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{
                        
        // get authorization header 
        String header = request.getHeader("Authorization");

        // Check true header
        if (header == null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // get pure token
        String token = header.substring(7);

        try {
            // parse and get claims
            Claims claims = jwtUtils.parseToken(token);
            logger.info("Claims getted");

            // get props
            // id and name in principal    
            Map<String,Object> principal = new HashMap<>();
            principal.put("id", (Integer) claims.get("id"));
            principal.put("username", claims.getSubject());
            logger.info("Properties getted");

            // roles in authorities
            List<String> roles = (List<String>) claims.get("roles");
            // List of String to List of Rights
            // Преобразуем список строк в список прав (GrantedAuthority)
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
            logger.info("Authorities added");

            // make Auth-object
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            logger.info("Token created");

            // set im context
            SecurityContextHolder.getContext().setAuthentication(auth);
            logger.info("Token registriert in system");
        } 
        catch (JwtException | IllegalArgumentException e) {
            // clear context, if token is invalid
            SecurityContextHolder.clearContext();
        }
        // continue request-process
        filterChain.doFilter(request, response);
    }
}

// достать ИД из контекста
// Map<String, Object> principal = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
// Long userId = ((Integer) principal.get("id")).longValue();
