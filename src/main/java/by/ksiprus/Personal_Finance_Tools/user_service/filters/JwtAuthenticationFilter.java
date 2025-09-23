package by.ksiprus.Personal_Finance_Tools.user_service.filters;

import by.ksiprus.Personal_Finance_Tools.config.AppConstants;
import by.ksiprus.Personal_Finance_Tools.user_service.constants.UserServiceConstants;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT фильтр аутентификации.
 * Обрабатывает JWT токены и устанавливает контекст безопасности.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final String BEARER_PREFIX = UserServiceConstants.BEARER_PREFIX;
    private static final String AUTHORIZATION_HEADER = UserServiceConstants.AUTHORIZATION_HEADER;
    private static final String SERVICE_TOKEN_TYPE = UserServiceConstants.TOKEN_TYPE_SERVICE;
    private static final String ROLE_PREFIX = UserServiceConstants.ROLE_PREFIX;
    private static final String JWT_TOKEN_TYPE_CLAIM = UserServiceConstants.JWT_TOKEN_TYPE_CLAIM;
    private static final String JWT_ROLE_CLAIM = UserServiceConstants.JWT_ROLE_CLAIM;
    
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (log.isDebugEnabled()) {
            log.debug("Processing request to: {}. Has auth header: {}", 
                     request.getRequestURI(), authHeader != null);
        }

        if (!hasValidAuthHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = extractToken(authHeader);
        
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateWithToken(token, request);
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * Проверяет наличие и формат заголовка авторизации.
     */
    private boolean hasValidAuthHeader(String authHeader) {
        return StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX);
    }
    
    /**
     * Извлекает JWT токен из заголовка.
     */
    private String extractToken(String authHeader) {
        return authHeader.substring(BEARER_PREFIX.length());
    }
    
    /**
     * Аутентифицирует пользователя по JWT токену.
     */
    private void authenticateWithToken(String token, HttpServletRequest request) {
        try {
            Claims claims = jwtService.parseToken(token);
            
            if (log.isDebugEnabled()) {
                log.debug("JWT token parsed successfully for subject: {}", claims.getSubject());
            }

            UsernamePasswordAuthenticationToken auth = createAuthenticationToken(claims);
            auth.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            
        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
    
    /**
     * Создает объект аутентификации на основе claims из JWT.
     */
    private UsernamePasswordAuthenticationToken createAuthenticationToken(Claims claims) {
        String tokenType = claims.get(JWT_TOKEN_TYPE_CLAIM, String.class);
        
        if (SERVICE_TOKEN_TYPE.equals(tokenType)) {
            return createServiceAuthentication(claims);
        } else {
            return createUserAuthentication(claims);
        }
    }
    
    /**
     * Создает аутентификацию для сервисного токена.
     */
    private UsernamePasswordAuthenticationToken createServiceAuthentication(Claims claims) {
        String serviceName = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(
                serviceName,
                null,
                List.of(new SimpleGrantedAuthority(ROLE_PREFIX + "SERVICE"))
        );
    }
    
    /**
     * Создает аутентификацию для пользовательского токена.
     */
    private UsernamePasswordAuthenticationToken createUserAuthentication(Claims claims) {
        String uuid = claims.getSubject();
        UserRole role = UserRole.valueOf(claims.get(JWT_ROLE_CLAIM, String.class));
        return new UsernamePasswordAuthenticationToken(
                uuid,
                null,
                List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()))
        );
    }
}