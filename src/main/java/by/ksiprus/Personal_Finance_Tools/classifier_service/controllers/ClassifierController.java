package by.ksiprus.Personal_Finance_Tools.classifier_service.controllers;

import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateCurrencyRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request.CreateOperationCategoryRequest;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.Currency;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.OperationCategory;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfCurrency;
import by.ksiprus.Personal_Finance_Tools.classifier_service.models.PageOfOperationCategory;
import by.ksiprus.Personal_Finance_Tools.classifier_service.services.api.ICurrencyService;
import by.ksiprus.Personal_Finance_Tools.classifier_service.services.api.IOperationCategoryService;
import by.ksiprus.Personal_Finance_Tools.common.dto.ErrorResponse;
import by.ksiprus.Personal_Finance_Tools.common.dto.StructuredErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classifier")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Справочники", description = "Справочники для хранения системных классификаторов")
public class ClassifierController {
    
    private final ICurrencyService currencyService;
    private final IOperationCategoryService operationCategoryService;
    
    /**
     * Создание новой валюты
     */
    @PostMapping("/currency")
    @Operation(
        summary = "Добавление новой валюты",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Валюта добавлена в справочник"),
        @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(oneOf = {ErrorResponse.class, StructuredErrorResponse.class}))),
        @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
        @ApiResponse(responseCode = "403", description = "Данному токенту авторизации запрещено выполнять запроса на данный адрес"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Сервер не смог корректно обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Currency> createCurrency(@Valid @RequestBody CreateCurrencyRequest request) {
        log.info("Creating currency with title: {}", request.getTitle());
        
        Currency currency = currencyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(currency);
    }
    
    /**
     * Получение страницы валют
     */
    @GetMapping("/currency")
    @Operation(summary = "Получить страницу валют")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = PageOfCurrency.class))),
        @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
        @ApiResponse(responseCode = "403", description = "Данному токенту авторизации запрещено выполнять запроса на данный адрес"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Сервер не смог корректно обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageOfCurrency> getCurrencyPage(
            @Parameter(description = "Номер страницы")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Размер страницы")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        log.info("Getting currency page: page={}, size={}", page, size);
        
        if (page < 0) {
            throw new IllegalArgumentException("Номер страницы не может быть отрицательным");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Размер страницы должен быть от 1 до 100");
        }
        
        PageOfCurrency currencyPage = currencyService.getPage(page, size);
        return ResponseEntity.ok(currencyPage);
    }
    
    /**
     * Создание новой категории операции
     */
    @PostMapping("/operation/category")
    @Operation(
        summary = "Добавление новой категории операции",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Категория добавлена в справочник"),
        @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(oneOf = {ErrorResponse.class, StructuredErrorResponse.class}))),
        @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
        @ApiResponse(responseCode = "403", description = "Данному токенту авторизации запрещено выполнять запроса на данный адрес"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Сервер не смог корректно обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<OperationCategory> createOperationCategory(@Valid @RequestBody CreateOperationCategoryRequest request) {
        log.info("Creating operation category with title: {}", request.getTitle());
        
        OperationCategory category = operationCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    /**
     * Получение страницы категорий операций
     */
    @GetMapping("/operation/category")
    @Operation(summary = "Получить страницу категорий")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = PageOfOperationCategory.class))),
        @ApiResponse(responseCode = "400", description = "Запрос некорректен. Сервер не может обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Для выполнения запроса на данный адрес требуется передать токен авторизации"),
        @ApiResponse(responseCode = "403", description = "Данному токенту авторизации запрещено выполнять запроса на данный адрес"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Сервер не смог корректно обработать запрос",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageOfOperationCategory> getOperationCategoryPage(
            @Parameter(description = "Номер страницы")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Размер страницы")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        log.info("Getting operation category page: page={}, size={}", page, size);
        
        if (page < 0) {
            throw new IllegalArgumentException("Номер страницы не может быть отрицательным");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Размер страницы должен быть от 1 до 100");
        }
        
        PageOfOperationCategory categoryPage = operationCategoryService.getPage(page, size);
        return ResponseEntity.ok(categoryPage);
    }
}