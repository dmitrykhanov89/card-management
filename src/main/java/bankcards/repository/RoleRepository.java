package bankcards.repository;

import bankcards.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <p>
 * Репозиторий для работы с сущностью роли {@link Role}.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Стандартные CRUD-операции (с помощью JpaRepository)</li>
 *     <li>Поиск роли по её имени</li>
 * </ul>
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * <p>Находит роль по её имени.</p>
     *
     * @param name имя роли
     * @return Optional с ролью, если она найдена
     */
    Optional<Role> findByName(String name);
}