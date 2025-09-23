package by.ksiprus.Personal_Finance_Tools.user_service.exceptions;

/**
 * Исключение, которое выбрасывается, когда пользователь не найден.
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}