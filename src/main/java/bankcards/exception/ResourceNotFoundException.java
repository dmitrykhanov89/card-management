package bankcards.exception;

/**
 * <p>
 * Исключение, сигнализирующее об отсутствии запрашиваемого ресурса.
 * </p>
 *
 * <p>Применяется, когда сущность не найдена в базе данных или другой системе, например:</p>
 * <ul>
 *     <li>Пользователь с указанным ID или username не найден</li>
 *     <li>Карта с заданным идентификатором отсутствует</li>
 *     <li>Перевод или другой объект не существует</li>
 * </ul>
 *
 * <p>При выбрасывании {@link ResourceNotFoundException} обычно возвращается HTTP 404 (Not Found) через {@link GlobalExceptionHandler}.</p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Создаёт новое исключение отсутствия ресурса с сообщением.
     *
     * @param message текстовое сообщение ошибки
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}