package by.ksiprus.Personal_Finance_Tools.user_service.services;

import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IMailerService;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.MailStorage;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MailerService implements IMailerService {

    private final MailStorage mailStorage;
    private final JavaMailSender mailSender;

    @Override
    public boolean verify(String mail, String code) {
        log.info("Начало верификации для email: {} с кодом: {}", mail, code);
        
        // Получаем сохраненный код из базы данных
        String storedCode = mailStorage.getCode(mail);
        log.debug("Полученный из базы код: {}", storedCode);
        
        // Проверяем, что код найден
        if (storedCode == null) {
            log.warn("Код верификации не найден для email: {}", mail);
            throw new IllegalArgumentException("Код верификации не найден для данного email");
        }
        
        // Проверяем совпадение кодов
        if (!storedCode.equals(code)) {
            log.warn("Неверный код верификации для email: {}. Ожидался: {}, получен: {}", mail, storedCode, code);
            throw new IllegalArgumentException("Неверный код верификации");
        }
        
        log.info("Коды совпадают, продолжаем верификацию для email: {}", mail);
        
        // Выполняем верификацию и обновление статуса пользователя
        boolean result = mailStorage.verify(mail);
        if (!result) {
            log.error("Ошибка при выполнении верификации для email: {}", mail);
            throw new IllegalArgumentException("Ошибка при верификации пользователя");
        }
        
        log.info("Верификация успешно завершена для email: {}", mail);
        return true;
    }

    @Override
    @Async("mailTaskExecutor")
    public void sendMail(String mail, String code) {
        try {
            log.info("Starting email sending process for {}", mail);
            log.info("VERIFICATION CODE for {}: {} ", mail, code);
            log.info("If you didn't receive the email, use this code: {}", code);
            
            if (mailSender == null) {
                log.error("JavaMailSender is NULL! Email service not configured properly");
                return;
            }
            
            try {
                String host = System.getProperty("spring.mail.host", System.getenv("MAIL_HOST"));
                String username = System.getProperty("spring.mail.username", System.getenv("MAIL_USERNAME"));
                String password = System.getProperty("spring.mail.password", System.getenv("GMAIL_APP_PASSWORD"));
                
                log.info("SMTP Host: {}", host != null ? host : "smtp.gmail.com (default)");
                log.info("SMTP Username: {}", username != null ? username : "testitacademy1@gmail.com (default)");
                log.info("SMTP Password: {}", password != null && !password.isEmpty() ? "[SET]" : "[NOT SET]");
                
                if (password == null || password.isEmpty()) {
                    log.error("КРИТИЧЕСКАЯ ОШИБКА: GMAIL_APP_PASSWORD не установлен!");
                    log.error("Установите переменную окружения: export GMAIL_APP_PASSWORD=your_app_password");
                }
            } catch (Exception e) {
                log.warn("Ошибка при проверке конфигурации: {}", e.getMessage());
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("testitacademy1@gmail.com", "Personal Finance Tools");
            helper.setTo(mail);
            helper.setSubject("Personal Finance Tools - Verification Code");
            
            String emailBody = String.format(
                "Hello!\n\n" +
                "Welcome to Personal Finance Tools!\n\n" +
                "Your verification code is: %s\n\n" +
                "Please use this code to verify your account and complete the registration process.\n\n" +
                "If you didn't request this registration, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Personal Finance Tools Team",
                code
            );
            
            helper.setText(emailBody);
            
            log.info("Попытка отправки email к {}...", mail);
            mailSender.send(message);
            
            log.info("Email with verification code {} sent successfully to {}", code, mail);
            mailStorage.incrementVerifiedMailCount(mail);
            
        } catch (Exception e) {
            log.error("Ошибка отправки email к {}: {}", mail, e.getMessage(), e);
            log.error("Тип ошибки: {}", e.getClass().getSimpleName());
            
            if (e.getMessage().contains("Authentication failed")) {
                log.error("ПРОБЛЕМА АУТЕНТИФИКАЦИИ: Неверный пароль приложения Gmail");
                log.error("Решение: Проверьте GMAIL_APP_PASSWORD и создайте новый пароль приложения");
            } else if (e.getMessage().contains("Connection")) {
                log.error("ПРОБЛЕМА СОЕДИНЕНИЯ: Не удается подключиться к SMTP серверу");
                log.error("Решение: Проверьте интернет-соединение и настройки файрвола");
            }
            
            log.warn("Отправка email не удалась, но код верификации: {}", code);
            log.warn("Вы можете использовать этот код для верификации, даже если email не был отправлен");
            
            try {
                mailStorage.incrementVerifiedMailCount(mail);
            } catch (Exception storageException) {
                log.error("Ошибка обновления счётчика email: {}", storageException.getMessage());
            }
        }
    }

}
