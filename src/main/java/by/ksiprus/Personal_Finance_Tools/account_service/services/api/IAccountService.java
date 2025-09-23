package by.ksiprus.Personal_Finance_Tools.account_service.services.api;

import by.ksiprus.Personal_Finance_Tools.account_service.dto.request.CreateAccountRequest;
import by.ksiprus.Personal_Finance_Tools.account_service.models.Account;
import by.ksiprus.Personal_Finance_Tools.account_service.models.PageOfAccount;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Интерфейс сервиса для работы со счетами
 */
public interface IAccountService {
    
    /**
     * Создать новый счет
     * @param request запрос на создание счета
     * @param userId идентификатор пользователя
     * @return созданный счет
     */
    Account createAccount(CreateAccountRequest request, UUID userId);
    
    /**
     * Получить страницу счетов пользователя
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница счетов
     */
    PageOfAccount getAccountsPage(UUID userId, Pageable pageable);
    
    /**
     * Получить счет по идентификатору
     * @param accountId идентификатор счета
     * @param userId идентификатор пользователя
     * @return счет
     */
    Account getAccountById(UUID accountId, UUID userId);
    
    /**
     * Обновить счет
     * @param accountId идентификатор счета
     * @param request запрос на обновление
     * @param userId идентификатор пользователя
     * @param dtUpdate версия для оптимистичной блокировки
     * @return обновленный счет
     */
    Account updateAccount(UUID accountId, CreateAccountRequest request, UUID userId, Long dtUpdate);
}