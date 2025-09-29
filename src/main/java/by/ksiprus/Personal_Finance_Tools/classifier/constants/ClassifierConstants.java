package by.ksiprus.Personal_Finance_Tools.classifier_service.constants;

/**
 * Константы для справочных сервисов.
 */
public final class ClassifierConstants {
    
    // Pagination constants
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;
    
    // String constants for annotations (must be compile-time constants)
    public static final String DEFAULT_PAGE_NUMBER_STRING = "0";
    public static final String DEFAULT_PAGE_SIZE_STRING = "20";
    
    private ClassifierConstants() {
        // Utility class - prevent instantiation
    }
}