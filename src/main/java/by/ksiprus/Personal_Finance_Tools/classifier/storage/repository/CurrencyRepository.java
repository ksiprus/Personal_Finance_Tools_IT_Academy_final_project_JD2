package by.ksiprus.Personal_Finance_Tools.classifier_service.storage.repository;

import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {
    
    /**
     * Поиск валюты по названию
     * @param title название валюты
     * @return валюта если найдена
     */
    Optional<CurrencyEntity> findByTitle(String title);
    
    /**
     * Проверка существования валюты по названию
     * @param title название валюты
     * @return true если валюта существует
     */
    boolean existsByTitle(String title);
}