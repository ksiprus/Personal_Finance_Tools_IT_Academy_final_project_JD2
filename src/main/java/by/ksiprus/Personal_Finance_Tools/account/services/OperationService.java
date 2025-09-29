package by.ksiprus.Personal_Finance_Tools.account.services;

import by.ksiprus.Personal_Finance_Tools.account.dto.request.CreateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account.dto.request.UpdateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account.exceptions.AccountNotFoundException;
import by.ksiprus.Personal_Finance_Tools.account.models.Operation;
import by.ksiprus.Personal_Finance_Tools.account.models.PageOfOperation;
import by.ksiprus.Personal_Finance_Tools.account.services.api.IOperationService;
import by.ksiprus.Personal_Finance_Tools.account.storage.entity.OperationEntity;
import by.ksiprus.Personal_Finance_Tools.account.storage.repository.AccountRepository;
import by.ksiprus.Personal_Finance_Tools.account.storage.repository.OperationRepository;
import by.ksiprus.Personal_Finance_Tools.account.utils.AccountMapperUtils;
import by.ksiprus.Personal_Finance_Tools.account.utils.AccountValidationUtils;
import by.ksiprus.Personal_Finance_Tools.audit.services.api.IAuditService;
import by.ksiprus.Personal_Finance_Tools.audit.models.enums.EssenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Сервис для работы с операциями на счетах
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OperationService implements IOperationService {
    
    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private final IAuditService auditService;
    
    @Override
    @Transactional
    public Operation createOperation(UUID accountId, CreateOperationRequest request, UUID userId) {
        log.info("Создание новой операции для счета {} пользователя {}: {}", 
                accountId, userId, request.getDescription());
        
        // Проверяем, что счет существует и принадлежит пользователю
        if (!accountRepository.findByUuidAndUserId(accountId, userId).isPresent()) {
            throw new AccountNotFoundException("Счет не найден или не принадлежит пользователю");
        }
        
        // Создаем новую операцию
        long currentTimeMillis = Instant.now().toEpochMilli();
        OperationEntity entity = OperationEntity.builder()
                .uuid(UUID.randomUUID())
                .dt_create(currentTimeMillis)
                .dt_update(currentTimeMillis)
                .date(request.getDate())
                .description(request.getDescription())
                .category(request.getCategory())
                .value(request.getValue())
                .currency(request.getCurrency())
                .accountId(accountId)
                .build();
        
        entity = operationRepository.save(entity);
        
        // Обновляем баланс счета
        updateAccountBalance(accountId);
        
        // Записываем аудит
        try {
            auditService.saveAudit(
                userId,
                String.format("Создана операция '%s' на сумму %s на счете %s", 
                    request.getDescription(), request.getValue(), accountId),
                EssenceType.OPERATION,
                entity.getUuid().toString()
            );
        } catch (Exception e) {
            log.error("Ошибка при записи аудита создания операции: {}", e.getMessage(), e);
        }
        
        log.info("Операция {} успешно создана для счета {}", entity.getUuid(), accountId);
        return AccountMapperUtils.mapOperationEntityToModel(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageOfOperation getOperationsPage(UUID accountId, UUID userId, Pageable pageable) {
        log.info("Получение страницы операций для счета {} пользователя {}, страница: {}, размер: {}", 
                accountId, userId, pageable.getPageNumber(), pageable.getPageSize());
        
        // Проверяем, что счет существует и принадлежит пользователю
        if (!accountRepository.findByUuidAndUserId(accountId, userId).isPresent()) {
            throw new AccountNotFoundException("Счет не найден или не принадлежит пользователю");
        }
        
        Page<OperationEntity> entityPage = operationRepository.findByAccountIdOrderByDateDesc(accountId, pageable);
        
        return PageOfOperation.builder()
                .number(entityPage.getNumber())
                .size(entityPage.getSize())
                .total_pages(entityPage.getTotalPages())
                .total_elements(entityPage.getTotalElements())
                .first(entityPage.isFirst())
                .last(entityPage.isLast())
                .number_of_elements(entityPage.getNumberOfElements())
                .content(entityPage.getContent().stream()
                        .map(AccountMapperUtils::mapOperationEntityToModel)
                        .toList())
                .build();
    }
    
    @Override
    @Transactional
    public Operation updateOperation(UUID accountId, UUID operationId, UpdateOperationRequest request, UUID userId) {
        log.info("Обновление операции {} для счета {} пользователя {}", operationId, accountId, userId);
        
        // Проверяем, что счет существует и принадлежит пользователю
        if (!accountRepository.findByUuidAndUserId(accountId, userId).isPresent()) {
            throw new AccountNotFoundException("Счет не найден или не принадлежит пользователю");
        }
        
        // Находим операцию
        OperationEntity entity = operationRepository.findByUuidAndAccountId(operationId, accountId)
                .orElseThrow(() -> new AccountNotFoundException("Операция не найдена"));
        
        // Проверяем версию для оптимистичной блокировки
        AccountValidationUtils.validateVersion(entity.getDt_update(), request.getDtUpdate());
        
        // Обновляем данные
        entity.setDate(request.getDate());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setValue(request.getValue());
        entity.setCurrency(request.getCurrency());
        entity.setDt_update(Instant.now().toEpochMilli());
        
        entity = operationRepository.save(entity);
        
        // Обновляем баланс счета
        updateAccountBalance(accountId);
        
        // Записываем аудит
        try {
            auditService.saveAudit(
                userId,
                String.format("Обновлена операция '%s' на счете %s", 
                    request.getDescription(), accountId),
                EssenceType.OPERATION,
                operationId.toString()
            );
        } catch (Exception e) {
            log.error("Ошибка при записи аудита обновления операции: {}", e.getMessage(), e);
        }
        
        log.info("Операция {} успешно обновлена", operationId);
        return AccountMapperUtils.mapOperationEntityToModel(entity);
    }
    
    @Override
    @Transactional
    public void deleteOperation(UUID accountId, UUID operationId, UUID userId, Long dtUpdate) {
        log.info("Удаление операции {} для счета {} пользователя {}", operationId, accountId, userId);
        
        // Проверяем, что счет существует и принадлежит пользователю
        if (!accountRepository.findByUuidAndUserId(accountId, userId).isPresent()) {
            throw new AccountNotFoundException("Счет не найден или не принадлежит пользователю");
        }
        
        // Находим операцию
        OperationEntity entity = operationRepository.findByUuidAndAccountId(operationId, accountId)
                .orElseThrow(() -> new AccountNotFoundException("Операция не найдена"));
        
        // Проверяем версию для оптимистичной блокировки
        if (!entity.getDt_update().equals(dtUpdate)) {
            throw new IllegalArgumentException("Версия операции устарела. Перезагрузите данные и попробуйте снова");
        }
        
        // Удаляем операцию
        operationRepository.delete(entity);
        
        // Обновляем баланс счета
        updateAccountBalance(accountId);
        
        // Записываем аудит
        try {
            auditService.saveAudit(
                userId,
                String.format("Удалена операция %s на счете %s", operationId, accountId),
                EssenceType.OPERATION,
                operationId.toString()
            );
        } catch (Exception e) {
            log.error("Ошибка при записи аудита удаления операции: {}", e.getMessage(), e);
        }
        
        log.info("Операция {} успешно удалена", operationId);
    }
    
    /**
     * Обновить баланс счета на основе всех операций
     */
    private void updateAccountBalance(UUID accountId) {
        BigDecimal newBalance = operationRepository.calculateAccountBalance(accountId);
        accountRepository.findById(accountId).ifPresent(account -> {
            account.setBalance(newBalance);
            account.setDt_update(Instant.now().toEpochMilli());
            accountRepository.save(account);
        });
    }
    
    // ==================== АДМИНИСТРАТИВНЫЕ МЕТОДЫ ====================
    
    @Override
    @Transactional
    public Operation updateOperationByAdmin(UUID accountId, UUID operationId, UpdateOperationRequest request) {
        log.info("Обновление операции {} для счета {} администратором", operationId, accountId);
        
        // Проверяем, что счет существует (без проверки владельца)
        if (!accountRepository.findById(accountId).isPresent()) {
            throw new AccountNotFoundException("Счет не найден");
        }
        
        // Находим операцию
        OperationEntity entity = operationRepository.findByUuidAndAccountId(operationId, accountId)
                .orElseThrow(() -> new AccountNotFoundException("Операция не найдена"));
        
        // Проверяем версию для оптимистичной блокировки
        AccountValidationUtils.validateVersion(entity.getDt_update(), request.getDtUpdate());
        
        // Обновляем данные
        entity.setDate(request.getDate());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setValue(request.getValue());
        entity.setCurrency(request.getCurrency());
        entity.setDt_update(Instant.now().toEpochMilli());
        
        entity = operationRepository.save(entity);
        
        // Обновляем баланс счета
        updateAccountBalance(accountId);
        
        log.info("Операция {} успешно обновлена администратором", operationId);
        return AccountMapperUtils.mapOperationEntityToModel(entity);
    }
    
    @Override
    @Transactional
    public void deleteOperationByAdmin(UUID accountId, UUID operationId, Long dtUpdate) {
        log.info("Удаление операции {} для счета {} администратором", operationId, accountId);
        
        // Проверяем, что счет существует (без проверки владельца)
        if (!accountRepository.findById(accountId).isPresent()) {
            throw new AccountNotFoundException("Счет не найден");
        }
        
        // Находим операцию
        OperationEntity entity = operationRepository.findByUuidAndAccountId(operationId, accountId)
                .orElseThrow(() -> new AccountNotFoundException("Операция не найдена"));
        
        // Проверяем версию для оптимистичной блокировки
        if (!entity.getDt_update().equals(dtUpdate)) {
            throw new IllegalArgumentException("Версия операции устарела. Перезагрузите данные и попробуйте снова");
        }
        
        // Удаляем операцию
        operationRepository.delete(entity);
        
        // Обновляем баланс счета
        updateAccountBalance(accountId);
        
        log.info("Операция {} успешно удалена администратором", operationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageOfOperation getOperationsPageByAdmin(UUID accountId, Pageable pageable) {
        log.info("Получение страницы операций для счета {} администратором, страница: {}, размер: {}", 
                accountId, pageable.getPageNumber(), pageable.getPageSize());
        
        // Проверяем, что счет существует (без проверки владельца)
        if (!accountRepository.findById(accountId).isPresent()) {
            throw new AccountNotFoundException("Счет не найден");
        }
        
        Page<OperationEntity> entityPage = operationRepository.findByAccountIdOrderByDateDesc(accountId, pageable);
        
        return PageOfOperation.builder()
                .number(entityPage.getNumber())
                .size(entityPage.getSize())
                .total_pages(entityPage.getTotalPages())
                .total_elements(entityPage.getTotalElements())
                .first(entityPage.isFirst())
                .last(entityPage.isLast())
                .number_of_elements(entityPage.getNumberOfElements())
                .content(entityPage.getContent().stream()
                        .map(AccountMapperUtils::mapOperationEntityToModel)
                        .toList())
                .build();
    }
}