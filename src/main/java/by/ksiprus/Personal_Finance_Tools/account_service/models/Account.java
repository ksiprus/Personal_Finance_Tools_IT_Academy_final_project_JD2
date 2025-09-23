package by.ksiprus.Personal_Finance_Tools.account_service.models;

import by.ksiprus.Personal_Finance_Tools.account_service.models.enums.AccountType;
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
 * Счёт
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Счёт")
public class Account extends BaseEssence {
    
    @JsonProperty("title")
    @Schema(description = "Наименование счёта", example = "Основной счет")
    private String title;
    
    @JsonProperty("description")
    @Schema(description = "Описание счёта", example = "Счет для ежедневных расходов")
    private String description;
    
    @JsonProperty("balance")
    @Schema(description = "Текущий баланс", accessMode = Schema.AccessMode.READ_ONLY, example = "1500.50")
    private BigDecimal balance;
    
    @JsonProperty("type")
    @Schema(description = "Тип счёта: CASH - Наличные деньги, BANK_ACCOUNT - Счёт в банке, BANK_DEPOSIT - Депозит в банке", 
            example = "CASH")
    private AccountType type;
    
    @JsonProperty("currency")
    @Schema(description = "Валюта счёта", format = "uuid", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID currency;
}