package by.ksiprus.Personal_Finance_Tools.user_service.services;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

/**
 * Сервис для валидации запросов.
 */
@Service
public class ValidationService {

    /**
     * Проверяет наличие ошибок валидации и возвращает сообщение об ошибках.
     * 
     * @param bindingResult результат валидации
     * @return сообщение об ошибках или null, если ошибок нет
     */
    public String getValidationErrorMessage(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return null;
        }
        
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }

    /**
     * Проверяет корректность параметров верификации.
     */
    public boolean isValidVerificationRequest(String code, String mail) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        if (mail == null || mail.trim().isEmpty()) {
            return false;
        }
        
        // Простая проверка формата email
        return mail.contains("@") && mail.contains(".");
    }
}