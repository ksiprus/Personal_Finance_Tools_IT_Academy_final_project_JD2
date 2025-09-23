package by.ksiprus.Personal_Finance_Tools.user_service.services;

import by.ksiprus.Personal_Finance_Tools.config.JwtProperties;
import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserServiceConstants;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Сервис для работы с JWT токенами.
 * Обеспечивает генерацию и парсинг токенов.
 */
@Service
@Slf4j
public class JwtService implements IJwtService {

    private static final int MIN_SECRET_KEY_LENGTH = 32;
    private static final char PADDING_CHAR = '0';
    
    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = createSecretKey(jwtProperties.getSecret());
        log.debug("Инициализация JWT сервиса завершена");
    }
    
    /**
     * Создает секретный ключ для подписи JWT.
     */
    private SecretKey createSecretKey(String secret) {
        String processedSecret = secret;
        if (secret.length() < MIN_SECRET_KEY_LENGTH) {
            processedSecret = String.format("%-" + MIN_SECRET_KEY_LENGTH + "s", secret)
                    .replace(' ', PADDING_CHAR);
            log.warn("Секретный ключ JWT короче {} символов, будет дополнен", MIN_SECRET_KEY_LENGTH);
        }
        return Keys.hmacShaKeyFor(processedSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(UUID uuid, String mail, UserRole role) {
        log.debug("Генерация JWT токена для пользователя: {}", uuid);
        
        return Jwts.builder()
                .subject(uuid.toString())
                .claim(UserServiceConstants.JWT_MAIL_CLAIM, mail)
                .claim(UserServiceConstants.JWT_ROLE_CLAIM, role.toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(jwtProperties.getExpiration())))
                .signWith(key)
                .compact();
    }

    @Override
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public long getExpirationTime() {
        return jwtProperties.getExpiration();
    }
}
