package by.ksiprus.Personal_Finance_Tools.user_service.services;

import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserControllerMessages;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.AdminCreateUserRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.models.User;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IUserService;
import by.ksiprus.Personal_Finance_Tools.user_service.utils.UserValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для административных операций с пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Создает пользователя от имени администратора.
     * 
     * @param request данные для создания пользователя
     * @return результат операции создания
     * @throws IllegalArgumentException если пользователь уже существует или данные невалидны
     */
    public boolean createUser(AdminCreateUserRequest request) {
        log.info("Creating user by admin: {}", request.getMail());

        // Проверяем существование пользователя
        validateUserNotExists(request.getMail());

        // Валидируем роль и статус
        UserRole role = UserValidationUtils.validateRole(request.getRole());
        UserStatus status = UserValidationUtils.validateStatus(request.getStatus());

        // Создаем запрос
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                request.getMail(),
                request.getFio(),
                role,
                status,
                passwordEncoder.encode(request.getPassword())
        );

        boolean created = userService.create(userCreateRequest);
        
        if (created) {
            log.info("User created successfully by admin: {}", request.getMail());
        } else {
            log.error("Failed to create user by admin: {}", request.getMail());
        }
        
        return created;
    }

    /**
     * Проверяет, что пользователь с данным email не существует.
     */
    private void validateUserNotExists(String email) {
        try {
            User existingUser = userService.getByMail(email);
            if (existingUser != null) {
                throw new IllegalArgumentException(UserControllerMessages.USER_ALREADY_EXISTS_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            throw e; // Пробрасываем наше исключение
        } catch (Exception e) {
            // Пользователь не найден - это нормально
            log.debug("User not found, can proceed with creation: {}", email);
        }
    }
}