package by.ksiprus.Personal_Finance_Tools.user_service.constants;

/**
 * Константы сообщений для контроллеров пользователей.
 */
public final class UserControllerMessages {

    // Error messages
    public static final String VALIDATION_ERROR_MESSAGE = "Запрос содержит некорректные данные. ";
    // Role and status validation messages
    public static final String INVALID_ROLE_MESSAGE = "Некорректная роль пользователя. Доступные роли: ADMIN, USER, MANAGER";
    public static final String INVALID_STATUS_MESSAGE = "Некорректный статус пользователя. Доступные статусы: WAITING_ACTIVATION, ACTIVE, DEACTIVATED";
    
    // Admin operations messages
    public static final String USER_ALREADY_EXISTS_MESSAGE = "Пользователь с данным email уже существует";
    public static final String USER_CREATED_MESSAGE = "Пользователь добавлен";
    
    public static final String SERVER_ERROR_MESSAGE = "Сервер не смог корректно обработать запрос";
    public static final String INVALID_REQUEST_MESSAGE = "Запрос некорректен. Сервер не может обработать запрос";
    public static final String AUTH_REQUIRED_MESSAGE = "Для выполнения запроса на данный адрес требуется передать токен авторизации";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Неверный email или пароль";
    public static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Данный email уже занят!";
    
    // Success messages
    public static final String REGISTRATION_SUCCESS_MESSAGE = "На указанный email отправлено письмо с кодом верификации";
    public static final String VERIFICATION_SUCCESS_MESSAGE = "Верификация прошла успешно!";

    private UserControllerMessages() {
        // Utility class - prevent instantiation
    }
}