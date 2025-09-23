package by.ksiprus.Personal_Finance_Tools.account_service.controllers;

import by.ksiprus.Personal_Finance_Tools.account_service.dto.request.CreateAccountRequest;
import by.ksiprus.Personal_Finance_Tools.account_service.dto.request.CreateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account_service.models.Account;
import by.ksiprus.Personal_Finance_Tools.account_service.models.PageOfAccount;
import by.ksiprus.Personal_Finance_Tools.account_service.models.PageOfOperation;
import by.ksiprus.Personal_Finance_Tools.account_service.services.api.IAccountService;
import by.ksiprus.Personal_Finance_Tools.account_service.services.api.IOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер для работы со счетами пользователей
 */
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Счета", description = "Информация о Ваших личных счетах в которых вы храните ваши деньги")
public class AccountController {
    
    private final IAccountService accountService;
    private final IOperationService operationService;
    
    @Operation(summary = "Добавление нового счёта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Счёт добавлен в ваш профиль"),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("Получен запрос на создание счета: {}", request.getTitle());
        
        UUID userId = getCurrentUserId();
        log.info("ID текущего пользователя: {}", userId);
        
        Account account = accountService.createAccount(request, userId);
        
        log.info("Счет {} успешно создан для пользователя {}", account.getUuid(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    @Operation(summary = "Получить страницу счетов текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", 
                        content = @Content(schema = @Schema(implementation = PageOfAccount.class))),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<PageOfAccount> getAccounts(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Получен запрос на получение страницы счетов, страница: {}, размер: {}", page, size);
        
        UUID userId = getCurrentUserId();
        log.info("Поиск счетов для пользователя: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        PageOfAccount accounts = accountService.getAccountsPage(userId, pageable);
        
        log.info("Найдено {} счетов для пользователя {}", accounts.getTotal_elements(), userId);
        
        return ResponseEntity.ok(accounts);
    }
    
    @Operation(summary = "Получить информацию по счёту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", 
                        content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{uuid}")
    public ResponseEntity<Account> getAccount(
            @Parameter(description = "Идентификатор счёта") @PathVariable UUID uuid) {
        
        log.info("Получен запрос на получение счета: {}", uuid);
        
        UUID userId = getCurrentUserId();
        Account account = accountService.getAccountById(uuid, userId);
        
        return ResponseEntity.ok(account);
    }
    
    @Operation(summary = "Редактировать информацию о счёте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счёт обновлён"),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{uuid}/dt_update/{dt_update}")
    public ResponseEntity<Account> updateAccount(
            @Parameter(description = "Идентификатор счёта") @PathVariable UUID uuid,
            @Parameter(description = "Дата последнего обновления записи") @PathVariable Long dt_update,
            @Valid @RequestBody CreateAccountRequest request) {
        
        log.info("Получен запрос на обновление счета: {}", uuid);
        
        UUID userId = getCurrentUserId();
        Account account = accountService.updateAccount(uuid, request, userId, dt_update);
        
        log.info("Счет {} успешно обновлен", uuid);
        return ResponseEntity.ok(account);
    }
    
    /**
     * Получить UUID текущего аутентифицированного пользователя из SecurityContext
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        
        String userIdString = (String) authentication.getPrincipal();
        return UUID.fromString(userIdString);
    }
    
    // ==================== ОПЕРАЦИИ ====================
    
    @Operation(summary = "Добавление операции по счёту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Операция добавлена к счёту"),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{uuid}/operation")
    public ResponseEntity<by.ksiprus.Personal_Finance_Tools.account_service.models.Operation> createOperation(
            @Parameter(description = "Идентификатор счёта в котором создаём операцию") @PathVariable UUID uuid,
            @Valid @RequestBody CreateOperationRequest request) {
        
        log.info("Получен запрос на создание операции для счета {}: {}", uuid, request.getDescription());
        
        UUID userId = getCurrentUserId();
        by.ksiprus.Personal_Finance_Tools.account_service.models.Operation operation = 
                operationService.createOperation(uuid, request, userId);
        
        log.info("Операция {} успешно создана для счета {}", operation.getUuid(), uuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }
    
    @Operation(summary = "Получить страницу операций по счёту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", 
                        content = @Content(schema = @Schema(implementation = PageOfOperation.class))),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{uuid}/operation")
    public ResponseEntity<PageOfOperation> getOperations(
            @Parameter(description = "Идентификатор счёта по которому получаем операции") @PathVariable UUID uuid,
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Получен запрос на получение операций для счета {}, страница: {}, размер: {}", uuid, page, size);
        
        UUID userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        PageOfOperation operations = operationService.getOperationsPage(uuid, userId, pageable);
        
        return ResponseEntity.ok(operations);
    }
    
    @Operation(summary = "Редактировать информацию об операции на счёте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция изменена"),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{uuid}/operation/{uuid_operation}/dt_update/{dt_update}")
    public ResponseEntity<by.ksiprus.Personal_Finance_Tools.account_service.models.Operation> updateOperation(
            @Parameter(description = "Идентификатор счёта в котором редактируем операцию") @PathVariable UUID uuid,
            @Parameter(description = "Идентификатор операции в котором редактируем") @PathVariable UUID uuid_operation,
            @Parameter(description = "Дата последнего обновления записи") @PathVariable Long dt_update,
            @Valid @RequestBody CreateOperationRequest request) {
        
        log.info("Получен запрос на обновление операции {} для счета {}", uuid_operation, uuid);
        
        UUID userId = getCurrentUserId();
        by.ksiprus.Personal_Finance_Tools.account_service.models.Operation operation = 
                operationService.updateOperation(uuid, uuid_operation, request, userId, dt_update);
        
        log.info("Операция {} успешно обновлена", uuid_operation);
        return ResponseEntity.ok(operation);
    }
    
    @Operation(summary = "Удалить операцию на счёте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция удалена"),
            @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос"),
            @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
            @ApiResponse(responseCode = "403", description = "Данному токену авторизации запрещено выполнять запрос на данный адрес"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{uuid}/operation/{uuid_operation}/dt_update/{dt_update}")
    public ResponseEntity<Void> deleteOperation(
            @Parameter(description = "Идентификатор счёта в котором редактируем операцию") @PathVariable UUID uuid,
            @Parameter(description = "Идентификатор операции в котором редактируем") @PathVariable UUID uuid_operation,
            @Parameter(description = "Дата последнего обновления записи") @PathVariable Long dt_update) {
        
        log.info("Получен запрос на удаление операции {} для счета {}", uuid_operation, uuid);
        
        UUID userId = getCurrentUserId();
        operationService.deleteOperation(uuid, uuid_operation, userId, dt_update);
        
        log.info("Операция {} успешно удалена", uuid_operation);
        return ResponseEntity.ok().build();
    }
}