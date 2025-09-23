package by.ksiprus.Personal_Finance_Tools.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailConfigurationChecker implements CommandLineRunner {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String mailUsername;
    
    @Value("${spring.mail.password:}")
    private String mailPassword;
    
    @Value("${spring.mail.host:}")
    private String mailHost;
    
    @Value("${spring.mail.port:}")
    private String mailPort;

    public EmailConfigurationChecker(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("🔍 Checking email configuration...");
        
        log.info("📧 Mail Host: {}", mailHost);
        log.info("📧 Mail Port: {}", mailPort);
        log.info("📧 Mail Username: {}", mailUsername);
        
        if (mailPassword == null || mailPassword.trim().isEmpty()) {
            log.error("❌ GMAIL_APP_PASSWORD environment variable is not set!");
            log.error("🔧 Please set GMAIL_APP_PASSWORD with your Gmail App Password");
            log.error("🔗 How to create App Password: https://support.google.com/accounts/answer/185833");
        } else {
            log.info("✅ Mail password is configured (length: {})", mailPassword.length());
        }
        
        if (mailSender != null) {
            log.info("✅ JavaMailSender bean is properly configured");
        } else {
            log.error("❌ JavaMailSender bean is not available");
        }
        
        log.info("🔍 Email configuration check completed");
    }
}