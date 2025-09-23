package by.ksiprus.Personal_Finance_Tools.user_service.utils;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для работы с пользовательскими ролями и статусами.
 * Обеспечивает валидацию и получение списков доступных значений.
 */
public final class UserValidationUtils {

    private UserValidationUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Проверяет корректность роли пользователя.
     * 
     * @param roleString строковое представление роли
     * @return UserRole если валидна
     * @throws IllegalArgumentException если роль некорректна
     */
    public static UserRole validateRole(String roleString) {
        try {
            return UserRole.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Некорректная роль пользователя. Доступные роли: " + getAvailableRoles()
            );
        }
    }

    /**
     * Проверяет корректность статуса пользователя.
     * 
     * @param statusString строковое представление статуса
     * @return UserStatus если валиден
     * @throws IllegalArgumentException если статус некорректен
     */
    public static UserStatus validateStatus(String statusString) {
        try {
            return UserStatus.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Некорректный статус пользователя. Доступные статусы: " + getAvailableStatuses()
            );
        }
    }

    /**
     * Возвращает список всех доступных ролей.
     * 
     * @return строка с перечислением ролей
     */
    public static String getAvailableRoles() {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    /**
     * Возвращает список всех доступных статусов.
     * 
     * @return строка с перечислением статусов
     */
    public static String getAvailableStatuses() {
        return Arrays.stream(UserStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}