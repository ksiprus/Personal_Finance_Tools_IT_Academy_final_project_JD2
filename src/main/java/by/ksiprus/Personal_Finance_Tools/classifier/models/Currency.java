package by.ksiprus.Personal_Finance_Tools.classifier_service.models;

import by.ksiprus.Personal_Finance_Tools.common.models.BaseEssence;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Валюта
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Currency extends BaseEssence {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
}