package bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Сущность пользователя системы.
 * </p>
 *
 * <p>Основные свойства:</p>
 * <ul>
 *     <li>Уникальный идентификатор пользователя</li>
 *     <li>Имя пользователя (уникальное)</li>
 *     <li>Пароль пользователя</li>
 *     <li>Набор ролей пользователя</li>
 * </ul>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * <p>Уникальный идентификатор пользователя.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <p>Имя пользователя (уникальное).</p>
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * <p>Пароль пользователя.</p>
     */
    @Column(nullable = false)
    private String password;

    /**
     * <p>Набор ролей пользователя.</p>
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}