package bankcards.service;

import bankcards.entity.Role;
import bankcards.entity.User;
import java.util.Optional;

public interface UserService {

    void createUser(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findById(Long id);
    Role getRoleByName(String name);
}