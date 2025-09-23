package by.ksiprus.Personal_Finance_Tools.user_service.controllers;

import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserControllerMessages;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.UserResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IUserService;
import by.ksiprus.Personal_Finance_Tools.user_service.utils.ControllerUtils;
import by.ksiprus.Personal_Finance_Tools.user_service.utils.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер профиля пользователя.
 * Обрабатывает операции получения и управления профилем.
 */
@RestController
@RequestMapping({"/cabinet", "/api/v1/cabinet"})
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
@Tag(name = "User Profile", description = "API для управления профилем пользователя")
public class UserProfileController {

    private final IUserService userService;

    @Operation(summary = "Получить информацию о себе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ControllerUtils.executeWithExceptionHandling(() -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseHelper.unauthorized(UserControllerMessages.AUTH_REQUIRED_MESSAGE);
            }

            String uuidString = (String) auth.getPrincipal();
            UUID userUuid = UUID.fromString(uuidString);
            UserResponse user = userService.getByUuid(userUuid);

            return ResponseHelper.ok(user);
        });
    }
}