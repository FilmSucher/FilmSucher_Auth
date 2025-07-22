package film_sucher.auth.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import film_sucher.auth.security.JWTFilter;
import film_sucher.auth.security.JWTUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final JWTUtils jwtUtils;

    public SecurityConfig (JWTUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(csrfCustomizer -> csrfCustomizer.disable())
            .authorizeHttpRequests(authorizeHttpRequestsCustomizer ->
                    authorizeHttpRequestsCustomizer
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JWTFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);   
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}