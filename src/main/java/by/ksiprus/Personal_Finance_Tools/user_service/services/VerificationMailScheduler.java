package by.ksiprus.Personal_Finance_Tools.user_service.services;

import by.ksiprus.Personal_Finance_Tools.user_service.services.api.IMailerService;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.api.IMailStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Шпланировщик для повторной отправки кодов верификации.
 * Автоматически отправляет письма каждые 5 минут.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationMailScheduler {

    private static final long RESEND_INTERVAL_MS = 5 * 60 * 1000L; // 5 minutes
    
    private final IMailStorage mailStorage;
    private final IMailerService mailerService;

    @Scheduled(fixedRate = RESEND_INTERVAL_MS)
    @Transactional
    public void resendUnverifiedMails() {
        log.debug("Запуск повторной отправки неподтвержденных писем");
        
        try {
            var unverifiedMails = mailStorage.getUnverifiedMailsAndCodes();
            
            if (unverifiedMails.isEmpty()) {
                log.debug("Нет неподтвержденных писем для отправки");
                return;
            }
            
            int processedCount = 0;
            for (Pair<String, String> pair : unverifiedMails) {
                try {
                    mailerService.sendMail(pair.getFirst(), pair.getSecond());
                    mailStorage.incrementVerifiedMailCount(pair.getFirst());
                    processedCount++;
                } catch (Exception e) {
                    log.error("Ошибка при отправке письма на {}: {}", pair.getFirst(), e.getMessage());
                }
            }
            
            log.info("Обработано {} неподтвержденных писем", processedCount);
            
        } catch (Exception e) {
            log.error("Ошибка при обработке неподтвержденных писем: {}", e.getMessage(), e);
        }
    }
}
