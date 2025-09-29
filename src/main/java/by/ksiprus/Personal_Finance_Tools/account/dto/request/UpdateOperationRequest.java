package by.ksiprus.Personal_Finance_Tools.account.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Запрос на обновление операции
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOperationRequest {
    
    @JsonProperty("date")
    @NotNull(message = "Дата операции обязательна")
    private Long date;
    
    @JsonProperty("description")
    @NotBlank(message = "Описание операции не может быть пустым")
    @Size(max = 500, message = "Описание операции не может превышать 500 символов")
    private String description;
    
    @JsonProperty("category")
    @NotNull(message = "Категория операции обязательна")
    private UUID category;
    
    @JsonProperty("value")
    @NotNull(message = "Сумма операции обязательна")
    @DecimalMin(value = "0.01", message = "Сумма операции должна быть больше 0")
    private BigDecimal value;
    
    @JsonProperty("currency")
    @NotNull(message = "Валюта операции обязательна")
    private UUID currency;
    
    @JsonProperty("dt_update")
    @NotNull(message = "Версия записи обязательна")
    private Long dtUpdate;
}