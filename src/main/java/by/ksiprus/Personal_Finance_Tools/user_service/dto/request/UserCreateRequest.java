package by.ksiprus.Personal_Finance_Tools.user_service.dto.request;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import by.ksiprus.Personal_Finance_Tools.user_service.validations.groups.OnCreate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UserCreateRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    UUID uuid;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long dt_create;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long dt_update;

    @NotBlank(groups = OnCreate.class)
    @Email(groups = OnCreate.class)
    String mail;

    @NotBlank(groups = OnCreate.class)
    String fio;

    @NotNull(groups = OnCreate.class)
    UserRole role;

    @NotNull(groups = OnCreate.class)
    UserStatus status;

    @NotBlank(groups = OnCreate.class)
    String password;

    @JsonCreator
    public UserCreateRequest(@JsonProperty("mail") String mail,
                      @JsonProperty("fio") String fio,
                      @JsonProperty("role") UserRole role,
                      @JsonProperty("status") UserStatus status,
                      @JsonProperty("password") String password) {
        this.uuid = UUID.randomUUID();
        this.dt_create = Instant.now().toEpochMilli();
        this.dt_update = Instant.now().toEpochMilli();
        this.mail = mail;
        this.fio = fio;
        this.role = role;
        this.status = status;
        this.password = password;
    }
}
