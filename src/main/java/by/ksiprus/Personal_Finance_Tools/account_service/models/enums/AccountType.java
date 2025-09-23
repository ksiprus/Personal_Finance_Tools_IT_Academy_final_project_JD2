package by.ksiprus.Personal_Finance_Tools.account_service.models.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Тип счёта
 */
@Schema(description = "Тип счёта")
public enum AccountType {
    /**
     * Наличные деньги
     */
    @Schema(description = "Наличные деньги")
    CASH,
    
    /**
     * Счёт в банке
     */
    @Schema(description = "Счёт в банке")
    BANK_ACCOUNT,
    
    /**
     * Депозит в банке
     */
    @Schema(description = "Депозит в банке")
    BANK_DEPOSIT
}