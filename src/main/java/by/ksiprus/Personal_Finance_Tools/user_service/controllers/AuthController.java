package by.ksiprus.Personal_Finance_Tools.user_service.controllers;

import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserControllerMessages;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserLoginRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserRegistrationRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.LoginResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.TokenResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.services.ValidationService;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IMailerService;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IUserService;
import by.ksiprus.Personal_Finance_Tools.user_service.services.UserService;
import by.ksiprus.Personal_Finance_Tools.user_service.utils.ControllerUtils;
import by.ksiprus.Personal_Finance_Tools.user_service.utils.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер аутентификации и авторизации.
 * Обрабатывает регистрацию, вход и верификацию пользователей.
 */
@RestController
@RequestMapping({"/cabinet"})
@AllArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API для аутентификации и авторизации")
public class AuthController {

    private final UserService userService;
    private final IMailerService mailerService;
    private final ValidationService validationService;

    @Operation(summary = "Регистрация пользователя", 
               description = "Регистрирует нового пользователя и отправляет код верификации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request, 
                                     BindingResult bindingResult) {
        return ControllerUtils.executeWithExceptionHandling(() -> {
            ResponseEntity<?> validationError = ControllerUtils.checkValidation(bindingResult);
            if (validationError != null) {
                return validationError;
            }
            
            userService.create(request);
            return ResponseHelper.successCreated(UserControllerMessages.REGISTRATION_SUCCESS_MESSAGE);
        });
    }

    @Operation(summary = "Верификация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь верифицирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/verification")
    public ResponseEntity<?> verify(
            @Parameter(description = "Код верификации") @RequestParam String code, 
            @Parameter(description = "Email для верификации") @RequestParam String mail) {
        
        try {
            if (!validationService.isValidVerificationRequest(code, mail)) {
                return ResponseHelper.badRequest(UserControllerMessages.INVALID_REQUEST_MESSAGE);
            }
            
            mailerService.verify(mail, code);
            
            return ResponseHelper.success(UserControllerMessages.VERIFICATION_SUCCESS_MESSAGE);
            
        } catch (Exception e) {
            log.error("Ошибка при верификации пользователя: {}", e.getMessage(), e);
            return ResponseHelper.internalServerError(UserControllerMessages.SERVER_ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Вход в систему", description = "Авторизация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вход выполнен успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request, BindingResult bindingResult) {
        try {
            String validationErrorMessage = validationService.getValidationErrorMessage(bindingResult);
            if (validationErrorMessage != null) {
                log.warn("Получен запрос на авторизацию с некорректными данными");
                return ResponseHelper.badRequest(UserControllerMessages.VALIDATION_ERROR_MESSAGE + validationErrorMessage);
            }
            
            LoginResponse loginResponse = userService.login(request.getMail(), request.getPassword());
            if (loginResponse == null) {
                log.warn("Неудачная попытка авторизации для: {}", request.getMail());
                
                // Проверяем, есть ли специфичная причина ошибки
                String specificError = userService.getLoginErrorMessage(request.getMail());
                String loginErrorMessage = specificError != null ? specificError : UserControllerMessages.INVALID_CREDENTIALS_MESSAGE;
                
                return ResponseHelper.unauthorized(loginErrorMessage);
            }

            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken(loginResponse.getAccessToken())
                    .build();
            
            return ResponseHelper.ok(tokenResponse);
                    
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            return ResponseHelper.internalServerError(UserControllerMessages.SERVER_ERROR_MESSAGE);
        }
    }

}