package by.ksiprus.Personal_Finance_Tools.user_service.services;

import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserControllerMessages;
import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserServiceConstants;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserRegistrationRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserUpdateByManagerRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserUpdateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.LoginResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.UserResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.exceptions.UserNotFoundException;
import by.ksiprus.Personal_Finance_Tools.user_service.models.User;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IJwtService;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IMailerService;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.api.IMailStorage;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IUserService;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.api.IUserStorage;
import by.ksiprus.Personal_Finance_Tools.utils.api.ICodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для управления пользователями.
 * Обеспечивает основной CRUD функционал и аутентификацию.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService, UserDetailsService {

    private static final String TOKEN_TYPE = UserServiceConstants.TOKEN_TYPE_BEARER;
    
    private final IUserStorage userStorage;
    private final IMailStorage mailStorage;
    private final IMailerService mailerService;
    private final ICodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;

    @Override
    public boolean create(UserCreateRequest userCreateRequest) {
        return userStorage.add(userCreateRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(UserRegistrationRequest userRegistrationRequest) {
        log.debug("Создание пользователя через регистрацию: {}", userRegistrationRequest.getMail());
        
        validateUserNotExists(userRegistrationRequest.getMail());
        
        UserCreateRequest userCreateRequest = createRegistrationRequest(userRegistrationRequest);
        userStorage.add(userCreateRequest);
        
        sendVerificationEmail(userRegistrationRequest.getMail());
        
        log.info("Пользователь создан через регистрацию: {}", userRegistrationRequest.getMail());
        return true;
    }
    
    /**
     * Проверяет, что пользователь с данным email не существует.
     */
    private void validateUserNotExists(String email) {
        User existingUser = userStorage.getByMail(email);
        if (existingUser != null) {
            throw new IllegalArgumentException(UserControllerMessages.EMAIL_ALREADY_EXISTS_MESSAGE);
        }
    }
    
    /**
     * Создает запрос на создание пользователя для регистрации.
     */
    private UserCreateRequest createRegistrationRequest(UserRegistrationRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        return new UserCreateRequest(
                request.getMail(),
                request.getFio(),
                UserServiceConstants.DEFAULT_REGISTRATION_ROLE,
                UserServiceConstants.DEFAULT_REGISTRATION_STATUS,
                hashedPassword
        );
    }
    
    /**
     * Отправляет письмо с кодом верификации.
     */
    private void sendVerificationEmail(String email) {
        String code = codeGenerator.generateCode();
        mailStorage.add(email, code);
        mailerService.sendMail(email, code);
        log.debug("Отправлен код верификации на: {}", email);
    }

    public boolean verify(String code, String mail) {
        return mailerService.verify(mail, code);
    }

    @Override
    public boolean update(UUID uuid, long dt_update, UserCreateRequest userCreateRequest) {
        return userStorage.update(uuid, dt_update, userCreateRequest);
    }

    /**
     * Обновляет данные пользователя менеджером (без изменения пароля).
     * 
     * @param uuid UUID пользователя
     * @param dtUpdate время последнего обновления
     * @param request данные для обновления без пароля
     * @return результат операции обновления
     */
    public boolean updateByManager(UUID uuid, long dtUpdate, UserUpdateByManagerRequest request) {
        log.debug("Обновление пользователя {} менеджером", uuid);
        
        // Получаем текущего пользователя для сохранения его пароля
        User existingUser = userStorage.getByUuid(uuid);
        if (existingUser == null) {
            throw new UserNotFoundException("Пользователь с UUID " + uuid + " не найден");
        }
        
        // Создаем запрос с сохранением текущего пароля
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                request.getMail(),
                request.getFio(),
                request.getRole(),
                request.getStatus(),
                existingUser.getPassword() // Сохраняем текущий пароль
        );
        
        boolean updated = userStorage.update(uuid, dtUpdate, userCreateRequest);
        
        if (updated) {
            log.info("Пользователь {} успешно обновлен менеджером", uuid);
        } else {
            log.warn("Не удалось обновить пользователя {} менеджером", uuid);
        }
        
        return updated;
    }

    /**
     * Обновляет данные пользователя с возможностью изменения пароля (только для админа).
     * 
     * @param uuid UUID пользователя
     * @param dtUpdate время последнего обновления
     * @param request данные для обновления с возможным паролем
     * @param isAdmin флаг, указывающий что обновление выполняет администратор
     * @return результат операции обновления
     */
    public boolean updateWithPasswordSupport(UUID uuid, long dtUpdate, UserUpdateRequest request, boolean isAdmin) {
        log.debug("Обновление пользователя {} с поддержкой пароля, isAdmin: {}", uuid, isAdmin);
        
        // Получаем текущего пользователя
        User existingUser = userStorage.getByUuid(uuid);
        if (existingUser == null) {
            throw new UserNotFoundException("Пользователь с UUID " + uuid + " не найден");
        }
        
        String password;
        if (isAdmin && request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            // Админ может изменить пароль
            password = passwordEncoder.encode(request.getPassword());
            log.debug("Админ изменяет пароль для пользователя {}", uuid);
        } else {
            // Сохраняем текущий пароль
            password = existingUser.getPassword();
            log.debug("Пароль сохраняется без изменений для пользователя {}", uuid);
        }
        
        // Создаем запрос для обновления
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                request.getMail(),
                request.getFio(),
                request.getRole(),
                request.getStatus(),
                password
        );
        
        boolean updated = userStorage.update(uuid, dtUpdate, userCreateRequest);
        
        if (updated) {
            log.info("Пользователь {} успешно обновлен, isAdmin: {}", uuid, isAdmin);
        } else {
            log.warn("Не удалось обновить пользователя {}, isAdmin: {}", uuid, isAdmin);
        }
        
        return updated;
    }

    @Override
    public Page<UserResponse> get(int page, int size) {
        Page<User> userPage = userStorage.get(page, size);
        return userPage.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse getByUuid(UUID uuid) {
        User user = userStorage.getByUuid(uuid);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с UUID " + uuid + " не найден");
        }
        return mapToUserResponse(user);
    }

    public User getByMail(String mail) {
        return userStorage.getByMail(mail);
    }

    @Override
    public LoginResponse login(String mail, String password) {
        log.debug("Попытка входа для пользователя: {}", mail);
        
        User user = userStorage.getByMail(mail);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return createLoginResponse(user);
        }
        
        log.warn("Неудачная попытка входа для: {}", mail);
        return null;
    }
    
    /**
     * Создает ответ для успешного входа.
     */
    private LoginResponse createLoginResponse(User user) {
        UserResponse userResponse = mapToUserResponse(user);
        String token = jwtService.generateToken(user.getUuid(), user.getMail(), user.getRole());
        
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType(TOKEN_TYPE)
                .expiresIn(jwtService.getExpirationTime())
                .user(userResponse)
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.getByMail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return user;
    }

    private UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .uuid(user.getUuid())
                .dt_create(user.getDt_create())
                .dt_update(user.getDt_update())
                .mail(user.getMail())
                .fio(user.getFio())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}