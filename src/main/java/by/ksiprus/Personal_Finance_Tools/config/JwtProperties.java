package by.ksiprus.Personal_Finance_Tools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secret;
    private long expiration;
}
/**
 * Конфигурационный класс для настроек JWT (JSON Web Token).
 *
 * @Configuration указывает, что это конфигурационный класс Spring
 * @ConfigurationProperties связывает свойства из application.properties/yaml с префиксом "jwt"
 *
 * Поля класса:
 * - secret: секретный ключ для подписи JWT токенов
 * - expiration: время жизни токена в секундах
 *
 * Используется для централизованного хранения и управления настройками JWT аутентификации
 * в приложении. Значения полей автоматически заполняются из конфигурационного файла.
 */