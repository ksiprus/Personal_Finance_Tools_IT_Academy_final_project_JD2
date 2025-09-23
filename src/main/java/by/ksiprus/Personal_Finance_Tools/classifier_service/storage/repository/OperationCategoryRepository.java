package by.ksiprus.Personal_Finance_Tools.classifier_service.storage.repository;

import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.entity.OperationCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationCategoryRepository extends JpaRepository<OperationCategoryEntity, UUID> {
    
    /**
     * Поиск категории операции по названию
     * @param title название категории
     * @return категория если найдена
     */
    Optional<OperationCategoryEntity> findByTitle(String title);
    
    /**
     * Проверка существования категории по названию
     * @param title название категории
     * @return true если категория существует
     */
    boolean existsByTitle(String title);
}