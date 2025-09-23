package by.ksiprus.Personal_Finance_Tools.user_service.utils;

import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserControllerMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.function.Supplier;

/**
 * Утилитарные методы для контроллеров.
 */
@Slf4j
public final class ControllerUtils {

    private ControllerUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Выполняет операцию с обработкой стандартных исключений.
     */
    public static ResponseEntity<?> executeWithExceptionHandling(Supplier<ResponseEntity<?>> operation) {
        try {
            return operation.get();
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseHelper.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseHelper.internalServerError(UserControllerMessages.SERVER_ERROR_MESSAGE);
        }
    }

    /**
     * Проверяет валидацию и возвращает ошибку, если она есть.
     */
    public static ResponseEntity<?> checkValidation(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(java.util.stream.Collectors.joining(", "));
            return ResponseHelper.badRequest(UserControllerMessages.VALIDATION_ERROR_MESSAGE + errorMessage);
        }
        return null; // Валидация прошла успешно
    }
}