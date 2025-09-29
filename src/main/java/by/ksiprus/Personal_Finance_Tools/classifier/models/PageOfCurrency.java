package by.ksiprus.Personal_Finance_Tools.classifier_service.models;

import by.ksiprus.Personal_Finance_Tools.common.models.BasePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Страница валют
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageOfCurrency extends BasePage {
    
    @JsonProperty("content")
    private List<Currency> content;
}