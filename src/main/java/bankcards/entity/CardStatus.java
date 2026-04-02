package bankcards.entity;

/**
 * <p>
 * Статусы банковской карты.
 * </p>
 *
 * <p>Возможные значения:</p>
 * <ul>
 *     <li>ACTIVE — карта активна и может использоваться для операций</li>
 *     <li>BLOCKED — карта заблокирована и операции по ней запрещены</li>
 *     <li>EXPIRED — срок действия карты истёк</li>
 * </ul>
 */
public enum CardStatus {
    ACTIVE,
    BLOCKED,
    EXPIRED
}