package by.ksiprus.Personal_Finance_Tools.account.dto.request;

import by.ksiprus.Personal_Finance_Tools.account.models.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Запрос на создание счёта
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {
    
    @JsonProperty("title")
    @NotBlank(message = "Наименование счёта не может быть пустым")
    @Size(max = 255, message = "Наименование счёта не может превышать 255 символов")
    private String title;
    
    @JsonProperty("description")
    @Size(max = 500, message = "Описание счёта не может превышать 500 символов")
    private String description;
    
    @JsonProperty("type")
    @NotNull(message = "Тип счёта обязателен")
    private AccountType type;
    
    @JsonProperty("currency")
    @NotNull(message = "Валюта счёта обязательна")
    private UUID currency;
}