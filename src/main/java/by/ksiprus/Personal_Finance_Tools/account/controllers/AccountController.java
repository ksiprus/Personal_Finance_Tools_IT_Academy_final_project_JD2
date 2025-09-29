package by.ksiprus.Personal_Finance_Tools.account.controllers;

import by.ksiprus.Personal_Finance_Tools.account.constants.AccountConstants;
import by.ksiprus.Personal_Finance_Tools.account.constants.AccountControllerMessages;
import by.ksiprus.Personal_Finance_Tools.account.dto.request.CreateAccountRequest;
import by.ksiprus.Personal_Finance_Tools.account.dto.request.CreateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account.dto.request.UpdateAccountRequest;
import by.ksiprus.Personal_Finance_Tools.account.dto.request.UpdateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account.models.Account;
import by.ksiprus.Personal_Finance_Tools.account.models.PageOfAccount;
import by.ksiprus.Personal_Finance_Tools.account.models.PageOfOperation;
import by.ksiprus.Personal_Finance_Tools.account.services.api.IAccountService;
import by.ksiprus.Personal_Finance_Tools.account.services.api.IOperationService;
import by.ksiprus.Personal_Finance_Tools.account.utils.AccountValidationUtils;
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
 * Контроллер для работы со счетами пользователей.
 * Предоставляет CRUD операции для счетов и операций по счетам.
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final IAccountService accountService;
    private final IOperationService operationService;
    
    // =========================== КОНСТАНТЫ ОТВЕТОВ API ===========================
    
    private static final String RESPONSE_201_ACCOUNT_CREATED = "Счёт добавлен в ваш профиль";
    private static final String RESPONSE_201_OPERATION_CREATED = "Операция добавлена к счёту";
    private static final String RESPONSE_200_OK = "OK";
    private static final String RESPONSE_200_ACCOUNT_UPDATED = "Счёт обновлён";
    private static final String RESPONSE_200_OPERATION_UPDATED = "Операция изменена";
    private static final String RESPONSE_200_OPERATION_DELETED = "Операция удалена";
    private static final String RESPONSE_400_BAD_REQUEST = "Запрос некорректен. Сервер не может обработать запрос";
    private static final String RESPONSE_401_UNAUTHORIZED = "Для выполнения запроса на данный адрес требуется передать токен авторизации";
    private static final String RESPONSE_403_FORBIDDEN = "Данному токену авторизации запрещено выполнять запрос на данный адрес";
    private static final String RESPONSE_409_CONFLICT = "Конфликт версий. Запись была изменена другим пользователем";
    private static final String RESPONSE_500_INTERNAL_ERROR = "Внутренняя ошибка сервера";
    
    // =========================== МЕТОДЫ РАБОТЫ СО СЧЕТАМИ ===========================

    /**
     * Получить UUID текущего аутентифицированного пользователя из SecurityContext.
     * 
     * @return UUID пользователя
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(AccountControllerMessages.USER_NOT_AUTHENTICATED_MESSAGE);
        }
        
        String userIdString = (String) authentication.getPrincipal();
        return UUID.fromString(userIdString);
    }
    
    /**
     * Логирование запроса с параметрами пагинации.
     */
    private void logPageRequest(String operation, int page, int size) {
        log.info("Получен запрос на {}, страница: {}, размер: {}", operation, page, size);
    }
    
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("Получен запрос на создание счета: {}", request.getTitle());
        
        UUID userId = getCurrentUserId();
        Account account = accountService.createAccount(request, userId);
        
        log.info("Счет {} успешно создан для пользователя {}", account.getUuid(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    @GetMapping
    public ResponseEntity<PageOfAccount> getAccounts(
            @RequestParam(defaultValue = AccountConstants.DEFAULT_PAGE_NUMBER_STRING) int page,
            @RequestParam(defaultValue = AccountConstants.DEFAULT_PAGE_SIZE_STRING) int size) {
        
        logPageRequest("получение страницы счетов", page, size);
        AccountValidationUtils.validatePageParameters(page, size);
        
        UUID userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        PageOfAccount accounts = accountService.getAccountsPage(userId, pageable);
        
        log.info("Найдено {} счетов для пользователя {}", accounts.getTotal_elements(), userId);
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/{uuid}")
    public ResponseEntity<Account> getAccount(@PathVariable UUID uuid) {
        
        log.info("Получен запрос на получение счета: {}", uuid);
        
        UUID userId = getCurrentUserId();
        Account account = accountService.getAccountById(uuid, userId);
        
        return ResponseEntity.ok(account);
    }
    
    @PutMapping("/{uuid}/dt_update/{dt_update}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable UUID uuid,
            @PathVariable Long dt_update,
            @Valid @RequestBody UpdateAccountRequest request) {
        
        log.info("Получен запрос на обновление счета: {}", uuid);
        
        UUID userId = getCurrentUserId();
        // Устанавливаем dt_update из path variable для оптимистичной блокировки
        request.setDtUpdate(dt_update);
        Account account = accountService.updateAccount(uuid, request, userId);
        
        log.info("Счет {} успешно обновлен", uuid);
        return ResponseEntity.ok(account);
    }
    
    @PutMapping("/{uuid}")
    public ResponseEntity<Account> updateAccountStandard(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateAccountRequest request) {
        
        log.info("Получен запрос на обновление счета (стандартный REST): {}", uuid);
        
        UUID userId = getCurrentUserId();
        Account account = accountService.updateAccount(uuid, request, userId);
        
        log.info("Счет {} успешно обновлен (стандартный REST)", uuid);
        return ResponseEntity.ok(account);
    }
    
    // ==================== ОПЕРАЦИИ ====================
    
    @PostMapping("/{uuid}/operation")
    public ResponseEntity<by.ksiprus.Personal_Finance_Tools.account.models.Operation> createOperation(
            @PathVariable UUID uuid,
            @Valid @RequestBody CreateOperationRequest request) {
        
        log.info("Получен запрос на создание операции для счета {}: {}", uuid, request.getDescription());
        
        UUID userId = getCurrentUserId();
        by.ksiprus.Personal_Finance_Tools.account.models.Operation operation =
                operationService.createOperation(uuid, request, userId);
        
        log.info("Операция {} успешно создана для счета {}", operation.getUuid(), uuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }
    
    @GetMapping("/{uuid}/operation")
    public ResponseEntity<PageOfOperation> getOperations(
            @PathVariable UUID uuid,
            @RequestParam(defaultValue = AccountConstants.DEFAULT_PAGE_NUMBER_STRING) int page,
            @RequestParam(defaultValue = AccountConstants.DEFAULT_PAGE_SIZE_STRING) int size) {
        
        log.info("Получен запрос на получение операций для счета {}, страница: {}, размер: {}", uuid, page, size);
        
        AccountValidationUtils.validatePageParameters(page, size);
        
        UUID userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        PageOfOperation operations = operationService.getOperationsPage(uuid, userId, pageable);
        
        return ResponseEntity.ok(operations);
    }
    
    @PutMapping("/{uuid}/operation/{uuid_operation}/dt_update/{dt_update}")
    public ResponseEntity<by.ksiprus.Personal_Finance_Tools.account.models.Operation> updateOperation(
            @PathVariable UUID uuid,
            @PathVariable UUID uuid_operation,
            @PathVariable Long dt_update,
            @Valid @RequestBody CreateOperationRequest request) {
        
        log.info("Получен запрос на обновление операции {} для счета {}", uuid_operation, uuid);
        
        UUID userId = getCurrentUserId();
        // Создаем UpdateOperationRequest для совместимости с новым сервисом
        UpdateOperationRequest updateRequest = new UpdateOperationRequest(
            request.getDate(),
            request.getDescription(),
            request.getCategory(), 
            request.getValue(),
            request.getCurrency(),
            dt_update
        );
        by.ksiprus.Personal_Finance_Tools.account.models.Operation operation =
                operationService.updateOperation(uuid, uuid_operation, updateRequest, userId);
        
        log.info("Операция {} успешно обновлена", uuid_operation);
        return ResponseEntity.ok(operation);
    }
    
    @DeleteMapping("/{uuid}/operation/{uuid_operation}/dt_update/{dt_update}")
    public ResponseEntity<Void> deleteOperation(
            @PathVariable UUID uuid,
            @PathVariable UUID uuid_operation,
            @PathVariable Long dt_update) {
        
        log.info("Получен запрос на удаление операции {} для счета {}", uuid_operation, uuid);
        
        UUID userId = getCurrentUserId();
        operationService.deleteOperation(uuid, uuid_operation, userId, dt_update);
        
        log.info("Операция {} успешно удалена", uuid_operation);
        return ResponseEntity.ok().build();
    }
}