package film_sucher.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//get data from application.properties
@ConfigurationProperties(prefix="token")
@Component
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class JWTProps {
    private String secret;
    private Long validity;
}
