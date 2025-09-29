package by.ksiprus.Personal_Finance_Tools.classifier_service.constants;

/**
 * Константы сообщений для контроллеров справочников.
 */
public final class ClassifierControllerMessages {

    // Success messages
    public static final String CURRENCY_CREATED_MESSAGE = "Валюта добавлена в справочник";
    public static final String OPERATION_CATEGORY_CREATED_MESSAGE = "Категория добавлена в справочник";
    
    // Error messages
    public static final String VALIDATION_ERROR_MESSAGE = "Запрос содержит некорректные данные";
    public static final String SERVER_ERROR_MESSAGE = "Сервер не смог корректно обработать запрос";
    public static final String INVALID_REQUEST_MESSAGE = "Запрос некорректен. Сервер не может обработать запрос";
    public static final String AUTH_REQUIRED_MESSAGE = "Для выполнения запроса на данный адрес требуется передать токен авторизации";
    public static final String ACCESS_DENIED_MESSAGE = "Данному токенту авторизации запрещено выполнять запроса на данный адрес";
    public static final String INVALID_PAGE_NUMBER_MESSAGE = "Номер страницы не может быть отрицательным";
    public static final String INVALID_PAGE_SIZE_MESSAGE = "Размер страницы должен быть от 1 до %d";

    private ClassifierControllerMessages() {
        // Utility class - prevent instantiation
    }
}