package by.ksiprus.Personal_Finance_Tools.account.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CreateOperationRequest {
    
    @JsonProperty("date")
    @NotNull(message = "Дата операции обязательна")
    private Long date;
    
    @JsonProperty("description")
    @Size(max = 500, message = "Описание операции не может превышать 500 символов")
    private String description;
    
    @JsonProperty("category")
    @NotNull(message = "Категория операции обязательна")
    private UUID category;
    
    @JsonProperty("value")
    @NotNull(message = "Сумма операции обязательна")
    private BigDecimal value;
    
    @JsonProperty("currency")
    @NotNull(message = "Валюта операции обязательна")
    private UUID currency;
}