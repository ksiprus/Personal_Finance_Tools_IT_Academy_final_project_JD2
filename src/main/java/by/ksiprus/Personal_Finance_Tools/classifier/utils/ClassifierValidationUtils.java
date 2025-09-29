package by.ksiprus.Personal_Finance_Tools.classifier_service.utils;

import by.ksiprus.Personal_Finance_Tools.classifier_service.constants.ClassifierConstants;
import by.ksiprus.Personal_Finance_Tools.classifier_service.constants.ClassifierControllerMessages;

/**
 * Утилиты для валидации запросов справочных сервисов.
 */
public final class ClassifierValidationUtils {
    
    /**
     * Валидирует параметры страницы.
     * 
     * @param page номер страницы
     * @param size размер страницы
     * @throws IllegalArgumentException если параметры некорректны
     */
    public static void validatePageParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException(ClassifierControllerMessages.INVALID_PAGE_NUMBER_MESSAGE);
        }
        if (size < ClassifierConstants.MIN_PAGE_SIZE || size > ClassifierConstants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                String.format(ClassifierControllerMessages.INVALID_PAGE_SIZE_MESSAGE, ClassifierConstants.MAX_PAGE_SIZE)
            );
        }
    }
    
    private ClassifierValidationUtils() {
        // Utility class - prevent instantiation
    }
}