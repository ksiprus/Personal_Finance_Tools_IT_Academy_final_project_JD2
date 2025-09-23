package by.ksiprus.Personal_Finance_Tools.account_service.storage.repository;

import by.ksiprus.Personal_Finance_Tools.account_service.storage.entity.OperationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для работы с операциями на счетах
 */
@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, UUID> {
    
    /**
     * Получить страницу операций для конкретного счета
     * @param accountId идентификатор счета
     * @param pageable параметры пагинации
     * @return страница операций
     */
    Page<OperationEntity> findByAccountIdOrderByDateDesc(UUID accountId, Pageable pageable);
    
    /**
     * Найти операцию по UUID и идентификатору счета
     * @param uuid идентификатор операции
     * @param accountId идентификатор счета
     * @return операция если найдена
     */
    Optional<OperationEntity> findByUuidAndAccountId(UUID uuid, UUID accountId);
    
    /**
     * Вычислить текущий баланс счета на основе всех операций
     * @param accountId идентификатор счета
     * @return сумма всех операций
     */
    @Query("SELECT COALESCE(SUM(o.value), 0) FROM OperationEntity o WHERE o.accountId = :accountId")
    BigDecimal calculateAccountBalance(@Param("accountId") UUID accountId);
    
    /**
     * Удалить все операции для указанного счета
     * @param accountId идентификатор счета
     */
    void deleteAllByAccountId(UUID accountId);
}