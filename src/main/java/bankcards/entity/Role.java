package bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * Сущность роли пользователя в системе.
 * </p>
 *
 * <p>Основные свойства:</p>
 * <ul>
 *     <li>Уникальный идентификатор роли</li>
 *     <li>Название роли (например, ROLE_USER, ROLE_ADMIN)</li>
 * </ul>
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /**
     * <p>Уникальный идентификатор роли.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <p>Название роли (уникальное).</p>
     */
    @Column(nullable = false, unique = true)
    private String name;
}