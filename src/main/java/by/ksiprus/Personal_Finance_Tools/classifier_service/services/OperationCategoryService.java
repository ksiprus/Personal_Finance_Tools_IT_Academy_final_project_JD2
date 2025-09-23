package by.ksiprus.Personal_Finance_Tools.classifier_service.services;

import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateOperationCategoryRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.OperationCategory;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfOperationCategory;
import by.ksiprus.Personal_Finance_Tools.classifier_service.services.api.IOperationCategoryService;
import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.entity.OperationCategoryEntity;
import by.ksiprus.Personal_Finance_Tools.classifier_service.storage.repository.OperationCategoryRepository;
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
public class OperationCategoryService implements IOperationCategoryService {
    
    private final OperationCategoryRepository operationCategoryRepository;
    
    @Override
    @Transactional
    public OperationCategory create(CreateOperationCategoryRequest request) {
        log.info("Creating operation category with title: {}", request.getTitle());
        
        // Проверяем, что категория с таким названием не существует
        if (operationCategoryRepository.existsByTitle(request.getTitle())) {
            throw new IllegalArgumentException("Категория операции с названием '" + request.getTitle() + "' уже существует");
        }
        
        long currentTime = Instant.now().toEpochMilli();
        UUID uuid = UUID.randomUUID();
        
        OperationCategoryEntity entity = OperationCategoryEntity.builder()
                .uuid(uuid)
                .dt_create(currentTime)
                .dt_update(currentTime)
                .title(request.getTitle())
                .build();
        
        OperationCategoryEntity saved = operationCategoryRepository.save(entity);
        log.info("Operation category created successfully with UUID: {}", saved.getUuid());
        
        return mapToModel(saved);
    }
    
    @Override
    public PageOfOperationCategory getPage(int page, int size) {
        log.info("Getting operation category page: page={}, size={}", page, size);
        
        Page<OperationCategoryEntity> entityPage = operationCategoryRepository.findAll(PageRequest.of(page, size));
        
        List<OperationCategory> content = entityPage.getContent().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
        
        return PageOfOperationCategory.builder()
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
    public OperationCategory getByUuid(UUID uuid) {
        log.info("Getting operation category by UUID: {}", uuid);
        
        return operationCategoryRepository.findById(uuid)
                .map(this::mapToModel)
                .orElse(null);
    }
    
    @Override
    public OperationCategory getByTitle(String title) {
        log.info("Getting operation category by title: {}", title);
        
        return operationCategoryRepository.findByTitle(title)
                .map(this::mapToModel)
                .orElse(null);
    }
    
    private OperationCategory mapToModel(OperationCategoryEntity entity) {
        return OperationCategory.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .title(entity.getTitle())
                .build();
    }
}