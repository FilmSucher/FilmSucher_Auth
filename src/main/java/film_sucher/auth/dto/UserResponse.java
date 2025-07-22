package film_sucher.auth.dto;

import film_sucher.auth.entity.User;
import lombok.Data;

@Data
public class UserResponse{
    private Long id;
    private String username;
    private User.Role role;

    public UserResponse(Long id, String username, User.Role role){
        this.id = id;
        this.username = username;
        this.role = role;
    }
}