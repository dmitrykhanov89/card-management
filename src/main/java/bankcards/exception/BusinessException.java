package bankcards.exception;

/**
 * <p>
 * Исключение бизнес-логики.
 * </p>
 *
 * <p>Используется для сигнализации о нарушении правил приложения, например:</p>
 * <ul>
 *     <li>Попытка создать сущность с уже существующими уникальными данными</li>
 *     <li>Недопустимые операции пользователя (например, перевод с недостаточным балансом)</li>
 *     <li>Любые другие ошибки, не связанные с системными или техническими проблемами</li>
 * </ul>
 *
 * <p>При выбрасывании {@link BusinessException} обычно возвращается HTTP 400 (Bad Request) через {@link GlobalExceptionHandler}.</p>
 */
public class BusinessException extends RuntimeException {

    /**
     * Создаёт новое исключение бизнес-логики с сообщением.
     *
     * @param message текстовое сообщение ошибки
     */
    public BusinessException(String message) {
        super(message);
    }
}