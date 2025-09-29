package by.ksiprus.Personal_Finance_Tools.account.models;

import by.ksiprus.Personal_Finance_Tools.common.models.BasePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Страница операций
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageOfOperation extends BasePage {
    
    @JsonProperty("content")
    private List<Operation> content;
}