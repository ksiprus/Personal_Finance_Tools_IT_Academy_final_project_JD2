package by.ksiprus.Personal_Finance_Tools.classifier_service.services;

import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateCurrencyRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.Currency;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfCurrency;
import by.ksiprus.Personal_Finance_Tools.classifier_service.services.api.ICurrencyService;
import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.entity.CurrencyEntity;
import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.repository.CurrencyRepository;
import by.ksiprus.Personal_Finance_Tools.classifier_service.utils.ClassifierMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService implements ICurrencyService {
    
    private final CurrencyRepository currencyRepository;
    
    @Override
    @Transactional
    public Currency create(CreateCurrencyRequest request) {
        log.info("Creating currency with title: {}", request.getTitle());
        
        // Проверяем, что валюта с таким названием не существует
        if (currencyRepository.existsByTitle(request.getTitle())) {
            throw new IllegalArgumentException("Валюта с названием '" + request.getTitle() + "' уже существует");
        }
        
        long currentTime = Instant.now().toEpochMilli();
        UUID uuid = UUID.randomUUID();
        
        CurrencyEntity entity = CurrencyEntity.builder()
                .uuid(uuid)
                .dt_create(currentTime)
                .dt_update(currentTime)
                .title(request.getTitle().toUpperCase()) // Сохраняем в верхнем регистре
                .description(request.getDescription())
                .build();
        
        CurrencyEntity saved = currencyRepository.save(entity);
        log.info("Currency created successfully with UUID: {}", saved.getUuid());
        
        return ClassifierMapperUtils.mapCurrencyEntityToModel(saved);
    }
    
    @Override
    public PageOfCurrency getPage(int page, int size) {
        log.info("Getting currency page: page={}, size={}", page, size);
        
        Page<CurrencyEntity> entityPage = currencyRepository.findAll(PageRequest.of(page, size));
        
        List<Currency> content = entityPage.getContent().stream()
                .map(ClassifierMapperUtils::mapCurrencyEntityToModel)
                .collect(Collectors.toList());
        
        return PageOfCurrency.builder()
                .number(entityPage.getNumber())
                .size(entityPage.getSize())
                .total_pages(entityPage.getTotalPages())
                .total_elements(entityPage.getTotalElements())
                .first(entityPage.isFirst())
                .number_of_elements(entityPage.getNumberOfElements())
                .last(entityPage.isLast())
                .content(content)
                .build();
    }
    
    @Override
    public Currency getByUuid(UUID uuid) {
        log.info("Getting currency by UUID: {}", uuid);
        
        return currencyRepository.findById(uuid)
                .map(ClassifierMapperUtils::mapCurrencyEntityToModel)
                .orElse(null);
    }
    
    @Override
    public Currency getByTitle(String title) {
        log.info("Getting currency by title: {}", title);
        
        return currencyRepository.findByTitle(title.toUpperCase())
                .map(ClassifierMapperUtils::mapCurrencyEntityToModel)
                .orElse(null);
    }
}