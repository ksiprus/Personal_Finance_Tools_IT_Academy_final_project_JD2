package by.ksiprus.Personal_Finance_Tools.account.constants;

/**
 * Константы сообщений для контроллеров счетов.
 */
public final class AccountControllerMessages {

    // Сообщения об успехе
    public static final String ACCOUNT_CREATED_MESSAGE = "Счёт добавлен в ваш профиль";
    public static final String ACCOUNT_UPDATED_MESSAGE = "Счёт успешно обновлён";
    public static final String OPERATION_CREATED_MESSAGE = "Операция добавлена к счёту";
    public static final String OPERATION_UPDATED_MESSAGE = "Операция успешно обновлена";
    public static final String OPERATION_DELETED_MESSAGE = "Операция успешно удалена";
    public static final String ACCOUNT_ALREADY_EXISTS_MESSAGE = "Счёт уже существует!";
    
    // Сообщения об ошибках
    public static final String VALIDATION_ERROR_MESSAGE = "Запрос содержит некорректные данные";
    public static final String SERVER_ERROR_MESSAGE = "Сервер не смог корректно обработать запрос";
    public static final String INVALID_REQUEST_MESSAGE = "Запрос некорректен. Сервер не может обработать запрос";
    public static final String AUTH_REQUIRED_MESSAGE = "Для выполнения запроса на данный адрес требуется передать токен авторизации";
    public static final String ACCESS_DENIED_MESSAGE = "Данному токену авторизации запрещено выполнять запрос на данный адрес";
    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Счет не найден";
    public static final String OPERATION_NOT_FOUND_MESSAGE = "Операция не найдена";
    public static final String VERSION_CONFLICT_MESSAGE = "Версия записи устарела. Перезагрузите данные и попробуйте снова";
    public static final String USER_NOT_AUTHENTICATED_MESSAGE = "Пользователь не аутентифицирован";
    public static final String INVALID_PAGE_NUMBER_MESSAGE = "Номер страницы не может быть отрицательным";
    public static final String INVALID_PAGE_SIZE_MESSAGE = "Размер страницы должен быть от 1 до %d";

    private AccountControllerMessages() {
        // Утилитарный класс - предотвращаем создание экземпляров
    }
}