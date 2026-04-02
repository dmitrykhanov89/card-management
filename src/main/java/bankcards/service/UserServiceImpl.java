package bankcards.service;

import bankcards.dto.UserDTO;
import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.exception.BusinessException;
import bankcards.exception.ResourceNotFoundException;
import bankcards.repository.RoleRepository;
import bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * Реализация сервиса {@link UserService}, предоставляющая функциональность для управления пользователями.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание пользователей с назначением роли</li>
 *     <li>Поиск пользователей по имени или идентификатору</li>
 *     <li>Проверка существования пользователя</li>
 *     <li>Получение роли пользователя по имени</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new BusinessException("Роль не найдена: " + name));
    }

    public List<UserDTO> findAllDTO() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDTO findByUsernameDTO(String username) {
        return userRepository.findByUsername(username)
                .map(UserDTO::fromEntity).orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
    }
}