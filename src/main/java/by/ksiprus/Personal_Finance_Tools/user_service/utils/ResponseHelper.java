package by.ksiprus.Personal_Finance_Tools.user_service.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

/**
 * Утилитарный класс для создания стандартных HTTP ответов.
 * Устраняет дублирование кода в контроллерах.
 */
public final class ResponseHelper {

    private static final String ERROR_LOG_REF = "error";
    private static final String SUCCESS_LOG_REF = "success";

    private ResponseHelper() {
        // Utility class - prevent instantiation
    }

    /**
     * Создает успешный ответ с данными.
     */
    public static <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    /**
     * Создает успешный ответ с кодом 201 (Created) и данными.
     */
    public static <T> ResponseEntity<T> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    /**
     * Создает успешный ответ с сообщением.
     */
    public static ResponseEntity<?> success(String message) {
        return ResponseEntity.ok(createResponse(SUCCESS_LOG_REF, message));
    }

    /**
     * Создает успешный ответ с кодом 201 и сообщением.
     */
    public static ResponseEntity<?> successCreated(String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createResponse(SUCCESS_LOG_REF, message));
    }

    /**
     * Создает ответ об ошибке валидации (400).
     */
    public static ResponseEntity<?> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createResponse(ERROR_LOG_REF, message));
    }

    /**
     * Создает ответ об ошибке авторизации (401).
     */
    public static ResponseEntity<?> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createResponse(ERROR_LOG_REF, message));
    }

    /**
     * Создает ответ о запрете доступа (403).
     */
    public static ResponseEntity<?> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createResponse(ERROR_LOG_REF, message));
    }

    /**
     * Создает ответ о внутренней ошибке сервера (500).
     */
    public static ResponseEntity<?> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createResponse(ERROR_LOG_REF, message));
    }

    /**
     * Создает стандартный ответ с logref и message.
     */
    private static Map<String, String> createResponse(String logref, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("logref", logref);
        response.put("message", message);
        return response;
    }
}