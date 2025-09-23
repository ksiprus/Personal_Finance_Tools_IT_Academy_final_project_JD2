package by.ksiprus.Personal_Finance_Tools.config;

import by.ksiprus.Personal_Finance_Tools.user_service.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты кабинета
                        .requestMatchers("/cabinet/login", "/cabinet/registration", "/cabinet/verification").permitAll()
                        .requestMatchers("/api/v1/cabinet/login", "/api/v1/cabinet/registration", "/api/v1/cabinet/verification").permitAll()
                        
                        // Защищенные эндпоинты кабинета
                        .requestMatchers("/cabinet/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/cabinet/me").hasAnyRole("USER", "ADMIN")
                        
                        // Административные эндпоинты управления пользователями
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        
                        // Эндпоинты создания справочников (только для ADMIN и MANAGER)
                        .requestMatchers("/api/v1/classifier/currency").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/classifier/operation/category").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Эндпоинты чтения справочников (доступны всем аутентифицированным пользователям)
                        .requestMatchers("/api/v1/classifier/**").authenticated()
                        
                        // Аккаунты (доступны всем аутентифицированным пользователям)
                        .requestMatchers("/api/v1/account/**").authenticated()
                        
                        // Swagger UI и OpenAPI документация
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
