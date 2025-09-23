package by.ksiprus.Personal_Finance_Tools.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для создания пользователя администратором.
 */
@Data
public class AdminCreateUserRequest {
    
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String mail;
    
    @NotBlank(message = "ФИО не может быть пустым")
    private String fio;
    
    @NotBlank(message = "Роль не может быть пустой")
    private String role;
    
    @NotBlank(message = "Статус не может быть пустым")
    private String status;
    
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;
}