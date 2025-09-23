package by.ksiprus.Personal_Finance_Tools.account_service.services;

import by.ksiprus.Personal_Finance_Tools.account_service.dto.request.CreateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account_service.exceptions.AccountNotFoundException;
import by.ksiprus.Personal_Finance_Tools.account_service.models.Operation;
import by.ksiprus.Personal_Finance_Tools.account_service.models.PageOfOperation;
import by.ksiprus.Personal_Finance_Tools.account_service.services.api.IOperationService;
import by.ksiprus.Personal_Finance_Tools.account_service.storage.entity.OperationEntity;
import by.ksiprus.Personal_Finance_Tools.account_service.storage.repository.AccountRepository;
import by.ksiprus.Personal_Finance_Tools.account_service.storage.repository.OperationRepository;
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
        
        log.info("Операция {} успешно создана для счета {}", entity.getUuid(), accountId);
        return mapToModel(entity);
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
                        .map(this::mapToModel)
                        .toList())
                .build();
    }
    
    @Override
    @Transactional
    public Operation updateOperation(UUID accountId, UUID operationId, CreateOperationRequest request, 
                                   UUID userId, Long dtUpdate) {
        log.info("Обновление операции {} для счета {} пользователя {}", operationId, accountId, userId);
        
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
        
        log.info("Операция {} успешно обновлена", operationId);
        return mapToModel(entity);
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
    
    /**
     * Преобразование Entity в Model
     */
    private Operation mapToModel(OperationEntity entity) {
        return Operation.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .date(entity.getDate())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .value(entity.getValue())
                .currency(entity.getCurrency())
                .build();
    }
}