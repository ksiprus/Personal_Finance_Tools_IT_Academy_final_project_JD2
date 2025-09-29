package by.ksiprus.Personal_Finance_Tools.classifier_service.controllers;

import by.ksiprus.Personal_Finance_Tools.classifier_service.constants.ClassifierConstants;
import by.ksiprus.Personal_Finance_Tools.classifier_service.constants.ClassifierControllerMessages;
import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateCurrencyRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateOperationCategoryRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfCurrency;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfOperationCategory;
import by.ksiprus.Personal_Finance_Tools.classifier_service.services.api.ICurrencyService;
import by.ksiprus.Personal_Finance_Tools.classifier_service.services.api.IOperationCategoryService;
import by.ksiprus.Personal_Finance_Tools.classifier_service.utils.ClassifierValidationUtils;
import by.ksiprus.Personal_Finance_Tools.common.dto.ErrorResponse;
import by.ksiprus.Personal_Finance_Tools.common.dto.StructuredErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classifier")
@RequiredArgsConstructor
@Slf4j
public class ClassifierController {
    
    private final ICurrencyService currencyService;
    private final IOperationCategoryService operationCategoryService;
    
    @PostMapping("/currency")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> createCurrency(@Valid @RequestBody CreateCurrencyRequest request) {
        log.info("Creating currency with title: {}", request.getTitle());
        
        currencyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClassifierControllerMessages.CURRENCY_CREATED_MESSAGE);
    }
    
    @GetMapping("/currency")
    public ResponseEntity<PageOfCurrency> getCurrencyPage(
            @RequestParam(value = "page", defaultValue = ClassifierConstants.DEFAULT_PAGE_NUMBER_STRING) int page,
            @RequestParam(value = "size", defaultValue = ClassifierConstants.DEFAULT_PAGE_SIZE_STRING) int size) {
        
        log.info("Getting currency page: page={}, size={}", page, size);
        
        ClassifierValidationUtils.validatePageParameters(page, size);
        
        PageOfCurrency currencyPage = currencyService.getPage(page, size);
        return ResponseEntity.ok(currencyPage);
    }
    
    @PostMapping("/operation/category")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> createOperationCategory(@Valid @RequestBody CreateOperationCategoryRequest request) {
        log.info("Creating operation category with title: {}", request.getTitle());
        
        operationCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClassifierControllerMessages.OPERATION_CATEGORY_CREATED_MESSAGE);
    }
    
    @GetMapping("/operation/category")
    public ResponseEntity<PageOfOperationCategory> getOperationCategoryPage(
            @RequestParam(value = "page", defaultValue = ClassifierConstants.DEFAULT_PAGE_NUMBER_STRING) int page,
            @RequestParam(value = "size", defaultValue = ClassifierConstants.DEFAULT_PAGE_SIZE_STRING) int size) {
        
        log.info("Getting operation category page: page={}, size={}", page, size);
        
        ClassifierValidationUtils.validatePageParameters(page, size);
        
        PageOfOperationCategory categoryPage = operationCategoryService.getPage(page, size);
        return ResponseEntity.ok(categoryPage);
    }
}