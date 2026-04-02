package bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * Сущность банковской карты.
 * </p>
 *
 * <p>Основные свойства:</p>
 * <ul>
 *     <li>Уникальный идентификатор карты</li>
 *     <li>Номер карты</li>
 *     <li>Дата истечения срока действия</li>
 *     <li>Статус карты (например, ACTIVE, BLOCKED)</li>
 *     <li>Баланс карты</li>
 *     <li>Флаг запроса блокировки карты</li>
 *     <li>Владелец карты</li>
 * </ul>
 */
@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    /**
     * <p>Уникальный идентификатор карты.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <p>Номер карты (уникальный).</p>
     */
    @Column(nullable = false, unique = true)
    private String cardNumber;

    /**
     * <p>Дата истечения срока действия карты.</p>
     */
    @Column(nullable = false)
    private LocalDate expirationDate;

    /**
     * <p>Текущий статус карты.</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    /**
     * <p>Баланс карты.</p>
     */
    @Column(nullable = false)
    private BigDecimal balance;

    /**
     * <p>Флаг, указывающий, был ли запрошен запрос на блокировку карты.</p>
     */
    @Column(nullable = false)
    private boolean blockRequested = false;

    /**
     * <p>Флаг мягкого удаления карты.</p>
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * <p>Владелец карты.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}