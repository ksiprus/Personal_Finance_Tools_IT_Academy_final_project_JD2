package by.ksiprus.Personal_Finance_Tools.user_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ на запрос авторизации")
public class LoginResponse {
    
    @JsonProperty("access_token")
    @Schema(description = "JWT токен для авторизации", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @JsonProperty("token_type")
    @Schema(description = "Тип токена", example = "Bearer")
    private String tokenType;
    
    @JsonProperty("expires_in")
    @Schema(description = "Время жизни токена в секундах", example = "3600")
    private Long expiresIn;
    
    @JsonProperty("user")
    @Schema(description = "Информация о пользователе")
    private UserResponse user;
}