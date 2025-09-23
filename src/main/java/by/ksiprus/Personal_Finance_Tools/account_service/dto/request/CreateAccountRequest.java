package by.ksiprus.Personal_Finance_Tools.account_service.dto.request;

import by.ksiprus.Personal_Finance_Tools.account_service.models.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на создание счёта")
public class CreateAccountRequest {
    
    @JsonProperty("title")
    @NotBlank(message = "Наименование счёта не может быть пустым")
    @Size(max = 255, message = "Наименование счёта не может превышать 255 символов")
    @Schema(description = "Наименование счёта", example = "Основной счет", required = true)
    private String title;
    
    @JsonProperty("description")
    @Size(max = 500, message = "Описание счёта не может превышать 500 символов")
    @Schema(description = "Описание счёта", example = "Счет для ежедневных расходов")
    private String description;
    
    @JsonProperty("type")
    @NotNull(message = "Тип счёта обязателен")
    @Schema(description = "Тип счёта", example = "CASH", required = true)
    private AccountType type;
    
    @JsonProperty("currency")
    @NotNull(message = "Валюта счёта обязательна")
    @Schema(description = "Валюта счёта", format = "uuid", 
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
    private UUID currency;
}