package by.ksiprus.Personal_Finance_Tools.user_service.utils;

import by.ksiprus.Personal_Finance_Tools.config.AdminUserProperties;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import by.ksiprus.Personal_Finance_Tools.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserCreator implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminUserProperties adminUserProperties;

    @Override
    public void run(String... args) throws Exception {
        if (!adminUserProperties.isAutoCreate()) {
            log.info("Admin user auto-creation is disabled");
            return;
        }

        // Check if admin already exists
        try {
            var existingAdmin = userService.getByMail(adminUserProperties.getEmail());
            if (existingAdmin != null) {
                log.info("Admin user already exists");
                return;
            }
        } catch (Exception e) {
            log.info("Admin user not found, creating...");
        }

        // Create admin user
        UserCreateRequest adminUser = new UserCreateRequest(
                adminUserProperties.getEmail(),
                adminUserProperties.getName(),
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                passwordEncoder.encode(adminUserProperties.getPassword())
        );

        boolean created = userService.create(adminUser);
        if (created) {
            log.info("Admin user created successfully with email: {}", adminUserProperties.getEmail());
            log.warn("SECURITY: Change the default admin password immediately!");
        } else {
            log.error("Failed to create admin user");
        }
    }
}
