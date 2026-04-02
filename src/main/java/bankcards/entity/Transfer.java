package bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * Сущность перевода между банковскими картами.
 * </p>
 *
 * <p>Основные свойства:</p>
 * <ul>
 *     <li>Уникальный идентификатор перевода</li>
 *     <li>Карта-отправитель</li>
 *     <li>Карта-получатель</li>
 *     <li>Сумма перевода</li>
 *     <li>Дата и время создания перевода</li>
 * </ul>
 */
@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    /**
     * <p>Уникальный идентификатор перевода.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <p>Карта-отправитель перевода.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;

    /**
     * <p>Карта-получатель перевода.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;

    /**
     * <p>Сумма перевода.</p>
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * <p>Дата и время создания перевода.</p>
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}