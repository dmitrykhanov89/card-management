package bankcards.service;

import bankcards.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role saveRole(Role role);
    Optional<Role> findByName(String name);
    List<Role> findAll();
}