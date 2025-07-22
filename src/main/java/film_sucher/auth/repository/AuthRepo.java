package film_sucher.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import film_sucher.auth.entity.User;

@Repository
public interface AuthRepo extends CrudRepository<User, Long>{

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}