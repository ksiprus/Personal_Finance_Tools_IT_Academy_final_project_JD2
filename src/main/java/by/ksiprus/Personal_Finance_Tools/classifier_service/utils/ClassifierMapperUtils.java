package by.ksiprus.Personal_Finance_Tools.classifier_service.utils;

import by.ksiprus.Personal_Finance_Tools.classifier_service.models.Currency;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.OperationCategory;
import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.entity.CurrencyEntity;
import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.entity.OperationCategoryEntity;

/**
 * Утилиты для маппинга между сущностями и моделями.
 */
public final class ClassifierMapperUtils {
    
    /**
     * Преобразует CurrencyEntity в Currency.
     */
    public static Currency mapCurrencyEntityToModel(CurrencyEntity entity) {
        return Currency.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .build();
    }
    
    /**
     * Преобразует OperationCategoryEntity в OperationCategory.
     */
    public static OperationCategory mapOperationCategoryEntityToModel(OperationCategoryEntity entity) {
        return OperationCategory.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .title(entity.getTitle())
                .build();
    }
    
    private ClassifierMapperUtils() {
        // Utility class - prevent instantiation
    }
}