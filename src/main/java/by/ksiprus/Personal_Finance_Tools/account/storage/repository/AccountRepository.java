package by.ksiprus.Personal_Finance_Tools.account.storage.repository;

import by.ksiprus.Personal_Finance_Tools.account.storage.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository для работы с счетами пользователей
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    
    /**
     * Получить страницу счетов конкретного пользователя
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница счетов
     */
    Page<AccountEntity> findByUserId(UUID userId, Pageable pageable);
    
    /**
     * Найти счет по UUID и идентификатору пользователя
     * @param uuid идентификатор счета
     * @param userId идентификатор пользователя
     * @return счет если найден
     */
    Optional<AccountEntity> findByUuidAndUserId(UUID uuid, UUID userId);
    
    /**
     * Проверить существование счета с указанным названием у пользователя
     * @param title название счета
     * @param userId идентификатор пользователя
     * @return true если счет существует
     */
    boolean existsByTitleAndUserId(String title, UUID userId);
}