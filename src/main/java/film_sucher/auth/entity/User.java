package film_sucher.auth.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq_gen")
    @SequenceGenerator(name = "users_id_seq_gen", sequenceName = "users_id_seq")
    private Long id;

    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    protected User(){}

    public User(String username, String password, Role role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public enum Role {
        ADMIN, USER
    }
}
