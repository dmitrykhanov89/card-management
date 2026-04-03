package bankcards.service;

import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Сервис для управления пользователями системы.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание новых пользователей</li>
 *     <li>Поиск пользователей по имени или идентификатору</li>
 *     <li>Проверка существования пользователя по имени</li>
 *     <li>Получение роли пользователя по имени</li>
 *     <li>Получение пользователей в виде DTO</li>
 * </ul>
 */
public interface UserService {

    /**
     * Создаёт нового пользователя в системе.
     *
     * @param user объект пользователя для создания
     */
    void createUser(User user);

    /**
     * Находит пользователя по имени.
     *
     * @param username имя пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Возвращает пользователя по имени в виде DTO.
     *
     * @param username имя пользователя
     * @return DTO пользователя
     * @throws bankcards.exception.ResourceNotFoundException если пользователь не найден
     */
    UserDTO findByUsernameDTO(String username);

    /**
     * Возвращает список всех пользователей в виде DTO.
     *
     * @return список DTO пользователей
     */
    List<UserDTO> findAllDTO();

    /**
     * Проверяет, существует ли пользователь с указанным именем.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean existsByUsername(String username);

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findById(Long id);

    /**
     * Получает роль пользователя по имени роли.
     *
     * @param name имя роли
     * @return объект роли
     */
    Role getRoleByName(String name);
}