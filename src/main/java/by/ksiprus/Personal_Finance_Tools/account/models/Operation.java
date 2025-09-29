package by.ksiprus.Personal_Finance_Tools.account.models;

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
 * Операция над счётом
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Operation extends BaseEssence {
    
    @JsonProperty("date")
    private Long date;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("category")
    private UUID category;
    
    @JsonProperty("value")
    private BigDecimal value;
    
    @JsonProperty("currency")
    private UUID currency;
}