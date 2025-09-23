package by.ksiprus.Personal_Finance_Tools.user_service.dto.response;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID uuid;
    private long dt_create;
    private long dt_update;
    private String mail;
    private String fio;
    private UserRole role;
    private UserStatus status;
}
