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
public class CreateOperationCategoryRequest {
    
    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 2, max = 100, message = "Название категории должно содержать от 2 до 100 символов")
    private String title;
}