package by.ksiprus.Personal_Finance_Tools.account.constants;

/**
 * Константы для сервиса счетов.
 */
public final class AccountConstants {
    
    // константы для пагинации
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;
    
    // Строковые константы для аннотаций (должны быть compile-time константами)
    public static final String DEFAULT_PAGE_NUMBER_STRING = "0";
    public static final String DEFAULT_PAGE_SIZE_STRING = "20";
    
    // HTTP заголовки
    public static final String IF_MATCH_HEADER = "If-Match";
    public static final String ETAG_HEADER = "ETag";
    
    private AccountConstants() {
        // Утилитарный класс - предотвращаем создание экземпляров
    }
}