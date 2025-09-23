package by.ksiprus.Personal_Finance_Tools.user_service.controllers;

import by.ksiprus.Personal_Finance_Tools.config.AppConstants;
import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserControllerMessages;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.AdminCreateUserRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.UserResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.services.AdminUserService;
import by.ksiprus.Personal_Finance_Tools.user_service.services.UserService;
import by.ksiprus.Personal_Finance_Tools.user_service.utils.ControllerUtils;
import by.ksiprus.Personal_Finance_Tools.user_service.utils.ResponseHelper;
import by.ksiprus.Personal_Finance_Tools.user_service.validations.groups.OnCreate;
import by.ksiprus.Personal_Finance_Tools.user_service.validations.groups.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для административного управления пользователями.
 * Доступен только администраторам.
 */
@RestController
@RequestMapping({"/admin/users", "/api/v1/admin/users"})
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User Management", description = "API для административного управления пользователями")
public class AdminUserController {

    private final UserService userService;
    private final AdminUserService adminUserService;

    @Operation(summary = "Создание пользователя", 
               description = "Создает нового пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> createUser(@Validated(OnCreate.class) @RequestBody UserCreateRequest request) {
        return ControllerUtils.executeWithExceptionHandling(() -> {
            userService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        });
    }

    @Operation(summary = "Создание пользователя с ролью и статусом", 
               description = "Создает нового пользователя с заданными ролью и статусом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create")
    public ResponseEntity<?> createUserWithRoleAndStatus(@Valid @RequestBody AdminCreateUserRequest request) {
        return ControllerUtils.executeWithExceptionHandling(() -> {
            boolean created = adminUserService.createUser(request);
            return created 
                    ? ResponseHelper.successCreated(UserControllerMessages.USER_CREATED_MESSAGE)
                    : ResponseHelper.internalServerError(UserControllerMessages.SERVER_ERROR_MESSAGE);
        });
    }

    @Operation(summary = "Получение списка пользователей", 
               description = "Возвращает постраничный список пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Номер страницы") @RequestParam Optional<Integer> page, 
            @Parameter(description = "Размер страницы") @RequestParam Optional<Integer> size) {
        
        Page<UserResponse> users = userService.get(
                page.orElse(AppConstants.DEFAULT_PAGE_NUMBER), 
                size.orElse(AppConstants.DEFAULT_PAGE_SIZE)
        );
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получение пользователя по ID", 
               description = "Возвращает данные конкретного пользователя. Админ может получать сведения о себе и о любых других пользователях")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getUserById(@Parameter(description = "UUID пользователя") @PathVariable UUID uuid) {
        UserResponse user = userService.getByUuid(uuid);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Обновление пользователя (администратор)", 
               description = "Обновляет все данные пользователя, включая пароль. Доступно только администраторам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{uuid}/dt_update/{dt_update}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "UUID пользователя") @PathVariable UUID uuid, 
            @Parameter(description = "Время последнего обновления") @PathVariable long dtUpdate,
            @Validated(OnUpdate.class) @RequestBody UserCreateRequest request) {
        
        return ControllerUtils.executeWithExceptionHandling(() -> {
            userService.update(uuid, dtUpdate, request);
            return ResponseHelper.success("Информация о пользователе обновлена (включая пароль)");
        });
    }
}