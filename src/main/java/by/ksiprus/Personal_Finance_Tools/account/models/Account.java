package by.ksiprus.Personal_Finance_Tools.account.models;

import by.ksiprus.Personal_Finance_Tools.account.models.enums.AccountType;
import by.ksiprus.Personal_Finance_Tools.common.models.BaseEssence;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Account extends BaseEssence {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("balance")
    private BigDecimal balance;
    
    @JsonProperty("type")
    private AccountType type;
    
    @JsonProperty("currency")
    private UUID currency;
}