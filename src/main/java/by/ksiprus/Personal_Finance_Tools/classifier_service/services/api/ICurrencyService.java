package by.ksiprus.Personal_Finance_Tools.classifier_service.services.api;

import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateCurrencyRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.Currency;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfCurrency;

import java.util.UUID;

public interface ICurrencyService {
    
    /**
     * Создание новой валюты
     * @param request данные для создания валюты
     * @return созданная валюта
     */
    Currency create(CreateCurrencyRequest request);
    
    /**
     * Получение страницы валют
     * @param page номер страницы
     * @param size размер страницы
     * @return страница валют
     */
    PageOfCurrency getPage(int page, int size);
    
    /**
     * Получение валюты по UUID
     * @param uuid идентификатор валюты
     * @return валюта если найдена
     */
    Currency getByUuid(UUID uuid);
    
    /**
     * Получение валюты по названию
     * @param title название валюты
     * @return валюта если найдена
     */
    Currency getByTitle(String title);
}