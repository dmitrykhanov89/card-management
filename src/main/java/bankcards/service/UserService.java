package bankcards.service;

import bankcards.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAll();

    Optional<User> findById(Long id);
}