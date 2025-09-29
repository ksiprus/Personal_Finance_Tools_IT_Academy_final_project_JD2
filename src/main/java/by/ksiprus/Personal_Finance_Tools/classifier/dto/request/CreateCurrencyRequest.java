package by.ksiprus.Personal_Finance_Tools.classifier_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCurrencyRequest {
    
    @NotBlank(message = "Название валюты не может быть пустым")
    @Size(min = 2, max = 10, message = "Название валюты должно содержать от 2 до 10 символов")
    private String title;
    
    @Size(max = 255, message = "Описание не может превышать 255 символов")
    private String description;
}