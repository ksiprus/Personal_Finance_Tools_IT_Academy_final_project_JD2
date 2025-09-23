package by.ksiprus.Personal_Finance_Tools.account_service.exceptions;

/**
 * Исключение, выбрасываемое когда счет не найден
 */
public class AccountNotFoundException extends RuntimeException {
    
    public AccountNotFoundException(String message) {
        super(message);
    }
    
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}