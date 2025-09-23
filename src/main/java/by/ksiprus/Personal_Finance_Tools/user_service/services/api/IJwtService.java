package by.ksiprus.Personal_Finance_Tools.user_service.services.api;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface IJwtService {
    String generateToken(UUID uuid, String mail, UserRole role);
    Claims parseToken(String token);
    long getExpirationTime();
}
