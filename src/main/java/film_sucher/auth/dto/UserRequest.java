package film_sucher.auth.dto;

import film_sucher.auth.entity.User;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private User.Role role;

    public UserRequest(String username, String password, User.Role role){
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
