package by.ksiprus.Personal_Finance_Tools.account_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Запрос на создание операции
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание операции")
public class CreateOperationRequest {
    
    @JsonProperty("date")
    @NotNull(message = "Дата операции обязательна")
    @Schema(description = "Дата/время операции (linux time)", format = "int64", 
            example = "1640995200000", required = true)
    private Long date;
    
    @JsonProperty("description")
    @Size(max = 500, message = "Описание операции не может превышать 500 символов")
    @Schema(description = "Описание операции", example = "Покупка продуктов в магазине")
    private String description;
    
    @JsonProperty("category")
    @NotNull(message = "Категория операции обязательна")
    @Schema(description = "Категория операции", format = "uuid", 
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
    private UUID category;
    
    @JsonProperty("value")
    @NotNull(message = "Сумма операции обязательна")
    @Schema(description = "Сумма операции: отрицательная сумма - списание, положительная - пополнение", 
            example = "-150.50", required = true)
    private BigDecimal value;
    
    @JsonProperty("currency")
    @NotNull(message = "Валюта операции обязательна")
    @Schema(description = "Валюта операции", format = "uuid", 
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
    private UUID currency;
}