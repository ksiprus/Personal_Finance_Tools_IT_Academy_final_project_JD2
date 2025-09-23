package by.ksiprus.Personal_Finance_Tools.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ошибка. Содержит общее описание ошибки
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ошибка. Содержит общее описание ошибки")
public class ErrorResponse {
    
    @JsonProperty("logref")
    @Schema(description = "Тип ошибки (предназначено для машинной обработки): error - Признак что ошибка не привязана к полю", 
            allowableValues = {"error"}, 
            example = "error")
    private String logref;
    
    @JsonProperty("message")
    @Schema(description = "Сообщение об ошибке", 
            example = "Запрос содержит некорректные данные. Измените запрос и отправьте его ещё раз")
    private String message;
}