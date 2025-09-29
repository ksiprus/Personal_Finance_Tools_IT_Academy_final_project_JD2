package by.ksiprus.Personal_Finance_Tools.account.controllers;

import by.ksiprus.Personal_Finance_Tools.account.constants.AccountControllerMessages;
import by.ksiprus.Personal_Finance_Tools.account.dto.request.UpdateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account.models.Operation;
import by.ksiprus.Personal_Finance_Tools.account.models.PageOfOperation;
import by.ksiprus.Personal_Finance_Tools.account.services.api.IOperationService;
import by.ksiprus.Personal_Finance_Tools.account.constants.AccountConstants;
import by.ksiprus.Personal_Finance_Tools.account.utils.AccountValidationUtils;
import by.ksiprus.Personal_Finance_Tools.audit.services.api.IAuditService;
import by.ksiprus.Personal_Finance_Tools.audit.models.enums.EssenceType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Административный контроллер для управления операциями.
 * Доступен только администраторам.
 * Позволяет администраторам управлять операциями всех пользователей.
 * Все действия администратора фиксируются в системе аудита.
 */
@RestController
@RequestMapping("/admin/operations")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminOperationController {
    
    private final IOperationService operationService;
    private final IAuditService auditService;
    
    // =========================== КОНСТАНТЫ ОТВЕТОВ API ===========================
    
    private static final String RESPONSE_200_OK = "OK";
    private static final String RESPONSE_200_OPERATION_UPDATED = "Операция изменена";
    private static final String RESPONSE_200_OPERATION_DELETED = "Операция удалена";
    private static final String RESPONSE_200_TEST_COMPLETED = "Тест выполнен";
    private static final String RESPONSE_400_BAD_REQUEST = "Запрос некорректен. Сервер не может обработать запрос";
    private static final String RESPONSE_401_UNAUTHORIZED = "Для выполнения запроса на данный адрес требуется передать токен авторизации";
    private static final String RESPONSE_403_FORBIDDEN = "Данному токену авторизации запрещено выполнять запрос на данный адрес";
    private static final String RESPONSE_404_NOT_FOUND = "Счёт не найден";
    private static final String RESPONSE_404_OPERATION_NOT_FOUND = "Операция или счёт не найден";
    private static final String RESPONSE_409_CONFLICT = "Конфликт версий. Операция была изменена другим пользователем";
    private static final String RESPONSE_500_INTERNAL_ERROR = "Внутренняя ошибка сервера";
    private static final String RESPONSE_500_SERVER_ERROR = "Ошибка сервера";
    
    // =========================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===========================
    
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
     * Сохранить запись аудита для действий администратора.
     * 
     * @param action описание действия
     * @param essenceType тип сущности
     * @param essenceId идентификатор сущности
     */
    private void saveAdminAudit(String action, EssenceType essenceType, String essenceId) {
        try {
            UUID currentUserId = getCurrentUserId();
            auditService.saveAudit(currentUserId, action, essenceType, essenceId);
            log.info("Запись аудита успешно сохранена: {}", action);
        } catch (Exception e) {
            log.error("Ошибка при сохранении записи аудита: {}", e.getMessage(), e);
        }
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<PageOfOperation> getOperationsByAccount(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = AccountConstants.DEFAULT_PAGE_NUMBER_STRING) int page,
            @RequestParam(defaultValue = AccountConstants.DEFAULT_PAGE_SIZE_STRING) int size) {
        
        log.info("Администратор запрашивает операции для счета {}, страница: {}, размер: {}", accountId, page, size);
        
        AccountValidationUtils.validatePageParameters(page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        PageOfOperation operations = operationService.getOperationsPageByAdmin(accountId, pageable);
        
        log.info("Найдено {} операций для счета {} (администратор)", operations.getTotal_elements(), accountId);
        
        return ResponseEntity.ok(operations);
    }
    
    @PutMapping("/account/{accountId}/operation/{operationId}")
    public ResponseEntity<Operation> updateOperation(
            @PathVariable UUID accountId,
            @PathVariable UUID operationId,
            @Valid @RequestBody UpdateOperationRequest request) {
        
        log.info("Администратор обновляет операцию {} для счета {}", operationId, accountId);
        
        Operation operation = operationService.updateOperationByAdmin(accountId, operationId, request);
        
        // Сохраняем аудит
        saveAdminAudit(
            String.format("Администратор обновил операцию '%s' на счете %s", request.getDescription(), accountId),
            EssenceType.OPERATION,
            operationId.toString()
        );
        
        log.info("Операция {} успешно обновлена администратором", operationId);
        return ResponseEntity.ok(operation);
    }
    
    @DeleteMapping("/account/{accountId}/operation/{operationId}")
    public ResponseEntity<Void> deleteOperation(
            @PathVariable UUID accountId,
            @PathVariable UUID operationId,
            @RequestParam Long dtUpdate) {
        
        log.info("Администратор удаляет операцию {} для счета {}", operationId, accountId);
        
        operationService.deleteOperationByAdmin(accountId, operationId, dtUpdate);
        
        // Сохраняем аудит
        saveAdminAudit(
            String.format("Администратор удалил операцию %s на счете %s", operationId, accountId),
            EssenceType.OPERATION,
            operationId.toString()
        );
        
        log.info("Операция {} успешно удалена администратором", operationId);
        return ResponseEntity.ok().build();
    }
}