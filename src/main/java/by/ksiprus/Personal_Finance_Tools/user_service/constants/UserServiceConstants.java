package by.ksiprus.Personal_Finance_Tools.user_service.constants;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;

/**
 * Константы для user_service.
 * Централизует все hardcoded значения и магические константы.
 */
public final class UserServiceConstants {
    
    private UserServiceConstants() {
        // Utility class - prevent instantiation
    }
    
    // JWT-related constants
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    public static final String TOKEN_TYPE_SERVICE = "SERVICE";
    public static final String JWT_ROLE_CLAIM = "role";
    public static final String JWT_TOKEN_TYPE_CLAIM = "token_type";
    public static final String JWT_MAIL_CLAIM = "mail";
    
    // Authentication constants
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final int BEARER_PREFIX_LENGTH = 7;
    
    // Default user registration values
    public static final UserRole DEFAULT_REGISTRATION_ROLE = UserRole.USER;
    public static final UserStatus DEFAULT_REGISTRATION_STATUS = UserStatus.WAITING_ACTIVATION;
    
    // Validation constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_NAME_LENGTH = 255;
}