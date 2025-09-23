package by.ksiprus.Personal_Finance_Tools.account_service.models;

import by.ksiprus.Personal_Finance_Tools.common.models.BaseEssence;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Операция над счётом
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Операция над счётом")
public class Operation extends BaseEssence {
    
    @JsonProperty("date")
    @Schema(description = "Дата/время операции (linux time)", format = "int64", example = "1640995200000")
    private Long date;
    
    @JsonProperty("description")
    @Schema(description = "Описание операции", example = "Покупка продуктов в магазине")
    private String description;
    
    @JsonProperty("category")
    @Schema(description = "Категория операции", format = "uuid", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID category;
    
    @JsonProperty("value")
    @Schema(description = "Сумма операции: отрицательная сумма - списание, положительная - пополнение", 
            example = "-150.50")
    private BigDecimal value;
    
    @JsonProperty("currency")
    @Schema(description = "Валюта операции", format = "uuid", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID currency;
}