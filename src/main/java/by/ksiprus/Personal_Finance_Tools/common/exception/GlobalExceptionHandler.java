package by.ksiprus.Personal_Finance_Tools.common.exception;

import by.ksiprus.Personal_Finance_Tools.account_service.exceptions.AccountAlreadyExistsException;
import by.ksiprus.Personal_Finance_Tools.account_service.exceptions.AccountNotFoundException;
import by.ksiprus.Personal_Finance_Tools.common.dto.ErrorResponse;
import by.ksiprus.Personal_Finance_Tools.common.dto.StructuredErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации для @Valid аннотаций
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StructuredErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Ошибки валидации: {}", ex.getMessage());
        
        List<StructuredErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> StructuredErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        
        StructuredErrorResponse response = StructuredErrorResponse.builder()
                .logref("structured_error")
                .errors(fieldErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка ошибок валидации для @Validated аннотаций
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<StructuredErrorResponse> handleBindExceptions(BindException ex) {
        log.warn("Ошибки привязки данных: {}", ex.getMessage());
        
        List<StructuredErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> StructuredErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        
        StructuredErrorResponse response = StructuredErrorResponse.builder()
                .logref("structured_error")
                .errors(fieldErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка ошибок валидации constraints
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StructuredErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Ошибки нарушения ограничений: {}", ex.getMessage());
        
        List<StructuredErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(violation -> StructuredErrorResponse.FieldError.builder()
                        .field(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());
        
        StructuredErrorResponse response = StructuredErrorResponse.builder()
                .logref("structured_error")
                .errors(fieldErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка ошибок неправильного типа аргументов
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Ошибка несоответствия типа аргумента: {} для параметра {}", ex.getValue(), ex.getName());
        
        ErrorResponse response = ErrorResponse.builder()
                .logref("error")
                .message("Запрос содержит некорректные данные. Измените запрос и отправьте его ещё раз")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка ошибок аутентификации
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Неверные учетные данные: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .logref("error")
                .message("Запрос содержит некорректные данные. Измените запрос и отправьте его ещё раз")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка ошибок доступа
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Отказ в доступе: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .logref("error")
                .message("Данному токену авторизации запрещено выполнять запросы на данный адрес")
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Обработка исключения - счет не найден
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        log.warn("Счет не найден: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .logref("error")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Обработка исключения - счет уже существует
     */
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex) {
        log.warn("Счет уже существует: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .logref("error")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Некорректные аргументы: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .logref("error")
                .message("Запрос содержит некорректные данные. Измените запрос и отправьте его ещё раз")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка всех остальных исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        
        ErrorResponse response = ErrorResponse.builder()
                .logref("error")
                .message("Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}