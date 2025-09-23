package by.ksiprus.Personal_Finance_Tools.classifier_service.models;

import by.ksiprus.Personal_Finance_Tools.common.models.BasePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Страница категорий операций
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Страница категорий операций")
public class PageOfOperationCategory extends BasePage {
    
    @JsonProperty("content")
    @Schema(description = "Содержимое страницы")
    private List<OperationCategory> content;
}