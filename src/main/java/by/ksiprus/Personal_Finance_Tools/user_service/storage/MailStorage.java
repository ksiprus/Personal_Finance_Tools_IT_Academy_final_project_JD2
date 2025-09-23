package by.ksiprus.Personal_Finance_Tools.user_service.storage;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.api.IMailStorage;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.entity.MailEntity;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.entity.UserEntity;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.repository.MailRepository;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class MailStorage implements IMailStorage {

    private final MailRepository mailRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public boolean add(String mail, String code) {
        UserEntity user = userRepository.findByMail(mail).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с данным email не найден")
        );

        MailEntity existingMailEntity = mailRepository.findByUser_Uuid(user.getUuid());
        if (existingMailEntity != null) {
            log.info("Удаляем старую запись верификации для пользователя: {}", user.getUuid());
            mailRepository.delete(existingMailEntity);
        }

        MailEntity existingMailByEmail = mailRepository.findByUser_Mail(mail);
        if (existingMailByEmail != null && !existingMailByEmail.getUuid().equals(user.getUuid())) {
            log.info("Удаляем старую запись верификации для email: {}", mail);
            mailRepository.delete(existingMailByEmail);
        }

        MailEntity newMailEntity = MailEntity.builder()
                .uuid(UUID.randomUUID())
                .user(user)
                .code(code)
                .verified(false)
                .emailCount(0)
                .dt_create(Instant.now().toEpochMilli())
                .build();
        
        mailRepository.save(newMailEntity);
        log.info("Создана новая запись верификации для пользователя: {} с кодом: {}", user.getUuid(), code);
        
        return true;
    }

    @Override
    @Transactional
    public boolean verify(String mail) {
        log.info("Начало обновления статуса верификации для email: {}", mail);

        try {
            // Находим пользователя
            UserEntity user = userRepository.findByMail(mail)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с данным email не найден"));
            log.debug("Найден пользователь: {} с статусом: {}", user.getUuid(), user.getStatus());

            // Находим запись верификации
            MailEntity mailEntity = mailRepository.findByUser_Uuid(user.getUuid());
            if (mailEntity == null) {
                log.warn("Код верификации не найден для пользователя: {}", user.getUuid());
                throw new IllegalArgumentException("Код верификации не найден для данного пользователя");
            }
            log.debug("Найдена запись верификации: {}, статус верификации: {}", mailEntity.getUuid(), mailEntity.isVerified());

            // Проверяем, что пользователь еще не верифицирован
            if (mailEntity.isVerified()) {
                log.warn("Пользователь с email {} уже верифицирован", mail);
                throw new IllegalArgumentException("Пользователь уже верифицирован");
            }

            // Отмечаем как верифицированный
            mailEntity.setVerified(true);
            mailEntity.setDt_verified(Instant.now().toEpochMilli());
            log.info("Отмечаем запись верификации как верифицированную для email: {}", mail);

            // Обновляем статус пользователя на АКТИВНЫЙ
            UserStatus oldStatus = user.getStatus();
            user.setStatus(UserStatus.ACTIVE);
            user.setDt_update(Instant.now().toEpochMilli());
            log.info("Обновляем статус пользователя с {} на {} для email: {}", oldStatus, UserStatus.ACTIVE, mail);

            // Сохраняем изменения
            mailRepository.save(mailEntity);
            userRepository.save(user);
            log.info("Изменения сохранены в базу данных для email: {}", mail);

            return true;
        } catch (Exception e) {
            log.error("Ошибка при верификации для email {}: {}", mail, e.getMessage(), e);
            throw new IllegalArgumentException("Ошибка при верификации: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getCode(String mail) {
        try {
            // Проверяем, что пользователь существует
            UserEntity user = userRepository.findByMail(mail)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с данным email не найден"));

            // Получаем запись верификации
            MailEntity mailEntity = mailRepository.findByUser_Uuid(user.getUuid());
            if (mailEntity == null) {
                throw new IllegalArgumentException("Код верификации не найден для данного пользователя");
            }

            // Проверяем, что код еще не был верифицирован
            if (mailEntity.isVerified()) {
                throw new IllegalArgumentException("Пользователь уже верифицирован");
            }

            return mailEntity.getCode();
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при получении кода верификации: " + e.getMessage());
        }
    }

    public List<Pair<String, String>> getUnverifiedMailsAndCodes() {
        List<Pair<String, String>> unverifiedMailsAndCodes = new ArrayList<>();
        for (MailEntity mailEntity : mailRepository.findAllByVerifiedFalse()) {
            if (mailEntity.getEmailCount() > 3) {
                continue;
            }
            unverifiedMailsAndCodes.add(Pair.of(mailEntity.getUser().getMail(), mailEntity.getCode()));
        }
        return unverifiedMailsAndCodes;

    }

    @Override
    @Transactional
    public void incrementVerifiedMailCount(String mail) {
        MailEntity mailEntity = mailRepository.findByUser_Mail(mail);
        if (mailEntity != null) {
            mailEntity.setEmailCount(mailEntity.getEmailCount() + 1);
            mailRepository.save(mailEntity);
            log.debug("Увеличен счетчик email для {}: {}", mail, mailEntity.getEmailCount());
        } else {
            log.warn("Не найдена запись верификации для увеличения счетчика: {}", mail);
        }
    }
}
