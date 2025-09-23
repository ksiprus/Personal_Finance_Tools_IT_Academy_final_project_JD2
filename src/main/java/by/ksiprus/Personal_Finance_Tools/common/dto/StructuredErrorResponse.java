package by.ksiprus.Personal_Finance_Tools.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Ошибка. Содержит описание ошибок с указанием на поле в теле или параметры в запросе
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ошибка. Содержит описание ошибок с указанием на поле в теле или параметры в запросе")
public class StructuredErrorResponse {
    
    @JsonProperty("logref")
    @Schema(description = "Тип ошибки (предназначено для машинной обработки): structured_error - Признак что ошибка привязана к полю", 
            allowableValues = {"structured_error"}, 
            example = "structured_error")
    private String logref;
    
    @JsonProperty("errors")
    @Schema(description = "Список ошибок")
    private List<FieldError> errors;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Ошибка поля")
    public static class FieldError {
        
        @JsonProperty("field")
        @Schema(description = "Наименование поля с которым связано сообщение", 
                example = "time_unit")
        private String field;
        
        @JsonProperty("message")
        @Schema(description = "Сообщение об ошибке", 
                example = "Должно быть положительным числом")
        private String message;
    }
}