package by.ksiprus.Personal_Finance_Tools.classifier_service.services.api;

import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateOperationCategoryRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.OperationCategory;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfOperationCategory;

import java.util.UUID;

public interface IOperationCategoryService {
    
    /**
     * Создание новой категории операции
     * @param request данные для создания категории
     * @return созданная категория
     */
    OperationCategory create(CreateOperationCategoryRequest request);
    
    /**
     * Получение страницы категорий
     * @param page номер страницы
     * @param size размер страницы
     * @return страница категорий
     */
    PageOfOperationCategory getPage(int page, int size);
    
    /**
     * Получение категории по UUID
     * @param uuid идентификатор категории
     * @return категория если найдена
     */
    OperationCategory getByUuid(UUID uuid);
    
    /**
     * Получение категории по названию
     * @param title название категории
     * @return категория если найдена
     */
    OperationCategory getByTitle(String title);
}