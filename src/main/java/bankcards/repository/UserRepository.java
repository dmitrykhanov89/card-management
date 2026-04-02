package bankcards.repository;

import bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <p>
 * Репозиторий для работы с сущностью пользователя {@link User}.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Стандартные CRUD-операции (с помощью JpaRepository)</li>
 *     <li>Поиск пользователя по имени</li>
 *     <li>Проверка существования пользователя по имени</li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * <p>Находит пользователя по имени.</p>
     *
     * @param username имя пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * <p>Проверяет, существует ли пользователь с указанным именем.</p>
     *
     * @param username имя пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean existsByUsername(String username);
}