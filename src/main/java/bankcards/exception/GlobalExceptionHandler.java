package bankcards.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Глобальный обработчик исключений для всего приложения.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Обработка ошибок бизнес-логики ({@link BusinessException}) с кодом 400</li>
 *     <li>Обработка ошибок отсутствия ресурса ({@link ResourceNotFoundException}) с кодом 404</li>
 *     <li>Обработка ошибок валидации DTO ({@link MethodArgumentNotValidException}) с кодом 400</li>
 *     <li>Формирование JSON-ответа с сообщением об ошибке</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link ResourceNotFoundException}.
     *
     * @param ex исключение
     * @return {@link ResponseEntity} с кодом 404 и сообщением об ошибке
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Обрабатывает исключение {@link BusinessException}.
     *
     * @param ex исключение
     * @return {@link ResponseEntity} с кодом 400 и сообщением об ошибке
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает ошибки валидации DTO (@Valid).
     *
     * @param ex исключение {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} с кодом 400 и списком полей с ошибками
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}