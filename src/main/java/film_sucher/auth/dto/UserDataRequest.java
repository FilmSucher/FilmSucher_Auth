package film_sucher.auth.dto;

import lombok.Data;

@Data
public class UserDataRequest{
    private String username;
    private String password;

    public UserDataRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
}