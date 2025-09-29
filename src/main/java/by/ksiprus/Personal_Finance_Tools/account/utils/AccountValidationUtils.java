package by.ksiprus.Personal_Finance_Tools.account.utils;

import by.ksiprus.Personal_Finance_Tools.account.constants.AccountConstants;
import by.ksiprus.Personal_Finance_Tools.account.constants.AccountControllerMessages;

/**
 * Утилиты для валидации запросов сервиса счетов.
 */
public final class AccountValidationUtils {
    
    /**
     * Валидирует параметры страницы.
     * 
     * @param page номер страницы
     * @param size размер страницы
     * @throws IllegalArgumentException если параметры некорректны
     */
    public static void validatePageParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException(AccountControllerMessages.INVALID_PAGE_NUMBER_MESSAGE);
        }
        if (size < AccountConstants.MIN_PAGE_SIZE || size > AccountConstants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                String.format(AccountControllerMessages.INVALID_PAGE_SIZE_MESSAGE, AccountConstants.MAX_PAGE_SIZE)
            );
        }
    }
    
    /**
     * Валидирует версию для оптимистичной блокировки.
     * 
     * @param currentVersion текущая версия в БД
     * @param requestVersion версия из запроса
     * @throws IllegalArgumentException если версии не совпадают
     */
    public static void validateVersion(Long currentVersion, Long requestVersion) {
        if (!currentVersion.equals(requestVersion)) {
            throw new IllegalArgumentException(AccountControllerMessages.VERSION_CONFLICT_MESSAGE);
        }
    }
    
    private AccountValidationUtils() {
        // Utility class - prevent instantiation
    }
}