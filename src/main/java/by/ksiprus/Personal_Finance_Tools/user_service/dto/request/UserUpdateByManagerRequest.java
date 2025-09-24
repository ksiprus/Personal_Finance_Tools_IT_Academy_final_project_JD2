package by.ksiprus.Personal_Finance_Tools.user_service.dto.request;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import by.ksiprus.Personal_Finance_Tools.user_service.validations.groups.OnUpdate;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для обновления пользователя менеджером.
 * Не содержит поле пароля, так как менеджеры не могут изменять пароли.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateByManagerRequest {

    @NotBlank(groups = OnUpdate.class, message = "Email не может быть пустым")
    @Email(groups = OnUpdate.class, message = "Некорректный формат email")
    private String mail;

    @NotBlank(groups = OnUpdate.class, message = "ФИО не может быть пустым")
    private String fio;

    @NotNull(groups = OnUpdate.class, message = "Роль не может быть пустой")
    private UserRole role;

    @NotNull(groups = OnUpdate.class, message = "Статус не может быть пустым")
    private UserStatus status;
}