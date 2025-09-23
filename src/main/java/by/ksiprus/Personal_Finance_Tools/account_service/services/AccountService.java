package by.ksiprus.Personal_Finance_Tools.account_service.services;

import by.ksiprus.Personal_Finance_Tools.account_service.dto.request.CreateAccountRequest;
import by.ksiprus.Personal_Finance_Tools.account_service.exceptions.AccountAlreadyExistsException;
import by.ksiprus.Personal_Finance_Tools.account_service.exceptions.AccountNotFoundException;
import by.ksiprus.Personal_Finance_Tools.account_service.models.Account;
import by.ksiprus.Personal_Finance_Tools.account_service.models.PageOfAccount;
import by.ksiprus.Personal_Finance_Tools.account_service.services.api.IAccountService;
import by.ksiprus.Personal_Finance_Tools.account_service.storage.entity.AccountEntity;
import by.ksiprus.Personal_Finance_Tools.account_service.storage.repository.AccountRepository;
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
 * Сервис для работы со счетами пользователей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements IAccountService {
    
    private final AccountRepository accountRepository;
    
    @Override
    @Transactional
    public Account createAccount(CreateAccountRequest request, UUID userId) {
        log.info("Создание нового счета для пользователя {}: {}", userId, request.getTitle());
        
        // Проверяем, что у пользователя нет счета с таким названием
        if (accountRepository.existsByTitleAndUserId(request.getTitle(), userId)) {
            throw new AccountAlreadyExistsException("Счет с названием '" + request.getTitle() + "' уже существует");
        }
        
        // Создаем новый счет
        long currentTimeMillis = Instant.now().toEpochMilli();
        AccountEntity entity = AccountEntity.builder()
                .uuid(UUID.randomUUID())
                .dt_create(currentTimeMillis)
                .dt_update(currentTimeMillis)
                .title(request.getTitle())
                .description(request.getDescription())
                .balance(BigDecimal.ZERO) // Новый счет создается с нулевым балансом
                .type(request.getType())
                .currency(request.getCurrency())
                .userId(userId)
                .build();
        
        entity = accountRepository.save(entity);
        
        log.info("Счет {} успешно создан для пользователя {}", entity.getUuid(), userId);
        return mapToModel(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageOfAccount getAccountsPage(UUID userId, Pageable pageable) {
        log.info("Получение страницы счетов для пользователя {}, страница: {}, размер: {}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<AccountEntity> entityPage = accountRepository.findByUserId(userId, pageable);
        
        log.info("Найдено {} счетов в базе данных для пользователя: {}", entityPage.getTotalElements(), userId);
        
        return PageOfAccount.builder()
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
    @Transactional(readOnly = true)
    public Account getAccountById(UUID accountId, UUID userId) {
        log.info("Получение счета {} для пользователя {}", accountId, userId);
        
        AccountEntity entity = accountRepository.findByUuidAndUserId(accountId, userId)
                .orElseThrow(() -> new AccountNotFoundException("Счет не найден"));
        
        return mapToModel(entity);
    }
    
    @Override
    @Transactional
    public Account updateAccount(UUID accountId, CreateAccountRequest request, UUID userId, Long dtUpdate) {
        log.info("Обновление счета {} для пользователя {}", accountId, userId);
        
        AccountEntity entity = accountRepository.findByUuidAndUserId(accountId, userId)
                .orElseThrow(() -> new AccountNotFoundException("Счет не найден"));
        
        // Проверяем версию для оптимистичной блокировки
        if (!entity.getDt_update().equals(dtUpdate)) {
            throw new IllegalArgumentException("Версия счета устарела. Перезагрузите данные и попробуйте снова");
        }
        
        // Проверяем, что название не занято другим счетом
        if (!entity.getTitle().equals(request.getTitle()) && 
            accountRepository.existsByTitleAndUserId(request.getTitle(), userId)) {
            throw new AccountAlreadyExistsException("Счет с названием '" + request.getTitle() + "' уже существует");
        }
        
        // Обновляем данные
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setType(request.getType());
        entity.setCurrency(request.getCurrency());
        entity.setDt_update(Instant.now().toEpochMilli());
        
        entity = accountRepository.save(entity);
        
        log.info("Счет {} успешно обновлен", accountId);
        return mapToModel(entity);
    }
    
    /**
     * Преобразование Entity в Model
     */
    private Account mapToModel(AccountEntity entity) {
        return Account.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .balance(entity.getBalance())
                .type(entity.getType())
                .currency(entity.getCurrency())
                .build();
    }
}