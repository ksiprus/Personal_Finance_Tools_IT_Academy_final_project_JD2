package by.ksiprus.Personal_Finance_Tools.user_service.dto.request;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class UserLoginRequest {
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    String mail;
    
    @NotBlank(message = "Пароль не может быть пустым")
    String password;
}
