package by.ksiprus.Personal_Finance_Tools.user_service.controllers;

import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserControllerMessages;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserUpdateByManagerRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserUpdateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.UserResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


/**
 * Контроллер для API пользователей.
 * Предоставляет полный CRUD для управления пользователями для администраторов и менеджеров.
 */
@RestController
@RequestMapping({"/users", "/api/v1/users"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "API для управления пользователями (только для ADMIN и MANAGER)")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение всех пользователей", 
               description = "Возвращает страницу с информацией о всех пользователях. Доступно только администраторам и менеджерам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница с пользователями"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Номер страницы (начиная с 0)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") 
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponse> users = userService.get(page, size);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Создание нового пользователя", 
               description = "Создает нового пользователя в системе. Доступно только администраторам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@Validated(OnCreate.class) @RequestBody UserCreateRequest request) {
        return ControllerUtils.executeWithExceptionHandling(() -> {
            userService.create(request);
            return ResponseHelper.successCreated(UserControllerMessages.USER_CREATED_MESSAGE);
        });
    }

    @Operation(summary = "Получение информации о пользователе", 
               description = "Возвращает информацию о пользователе по UUID. Доступно только администраторам и менеджерам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getUserInfo(@Parameter(description = "UUID пользователя") @PathVariable UUID uuid) {
        UserResponse user = userService.getByUuid(uuid);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Обновление информации о пользователе", 
               description = "Обновляет данные пользователя. Администратор может изменять пароль, менеджер - нет")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{uuid}/dt_update/{dtUpdate}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "UUID пользователя") @PathVariable UUID uuid, 
            @Parameter(description = "Время последнего обновления") @PathVariable long dtUpdate,
            @Validated(OnUpdate.class) @RequestBody UserUpdateRequest request) {
        
        return ControllerUtils.executeWithExceptionHandling(() -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            
            userService.updateWithPasswordSupport(uuid, dtUpdate, request, isAdmin);
            
            if (isAdmin) {
                return ResponseHelper.success("Информация о пользователе обновлена (включая пароль)");
            } else {
                return ResponseHelper.success("Информация о пользователе обновлена (без пароля)");
            }
        });
    }
}
