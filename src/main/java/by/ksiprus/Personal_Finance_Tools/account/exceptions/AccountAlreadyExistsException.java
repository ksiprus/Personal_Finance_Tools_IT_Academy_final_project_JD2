package by.ksiprus.Personal_Finance_Tools.account.exceptions;

/**
 * Исключение, выбрасываемое когда счет с таким названием уже существует у пользователя
 */
public class AccountAlreadyExistsException extends RuntimeException {
    
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
    
    public AccountAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}