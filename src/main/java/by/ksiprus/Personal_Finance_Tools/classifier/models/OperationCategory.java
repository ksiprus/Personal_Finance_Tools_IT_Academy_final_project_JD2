package by.ksiprus.Personal_Finance_Tools.classifier_service.models;

import by.ksiprus.Personal_Finance_Tools.common.models.BaseEssence;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Категория операции
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OperationCategory extends BaseEssence {
    
    @JsonProperty("title")
    private String title;
}