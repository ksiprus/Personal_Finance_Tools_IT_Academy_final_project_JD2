package by.ksiprus.Personal_Finance_Tools.account_service.services.api;

import by.ksiprus.Personal_Finance_Tools.account_service.dto.request.CreateOperationRequest;
import by.ksiprus.Personal_Finance_Tools.account_service.models.Operation;
import by.ksiprus.Personal_Finance_Tools.account_service.models.PageOfOperation;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Интерфейс сервиса для работы с операциями на счетах
 */
public interface IOperationService {
    
    /**
     * Создать новую операцию на счете
     * @param accountId идентификатор счета
     * @param request запрос на создание операции
     * @param userId идентификатор пользователя
     * @return созданная операция
     */
    Operation createOperation(UUID accountId, CreateOperationRequest request, UUID userId);
    
    /**
     * Получить страницу операций по счету
     * @param accountId идентификатор счета
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница операций
     */
    PageOfOperation getOperationsPage(UUID accountId, UUID userId, Pageable pageable);
    
    /**
     * Обновить операцию
     * @param accountId идентификатор счета
     * @param operationId идентификатор операции
     * @param request запрос на обновление
     * @param userId идентификатор пользователя
     * @param dtUpdate версия для оптимистичной блокировки
     * @return обновленная операция
     */
    Operation updateOperation(UUID accountId, UUID operationId, CreateOperationRequest request, 
                            UUID userId, Long dtUpdate);
    
    /**
     * Удалить операцию
     * @param accountId идентификатор счета
     * @param operationId идентификатор операции
     * @param userId идентификатор пользователя
     * @param dtUpdate версия для оптимистичной блокировки
     */
    void deleteOperation(UUID accountId, UUID operationId, UUID userId, Long dtUpdate);
}