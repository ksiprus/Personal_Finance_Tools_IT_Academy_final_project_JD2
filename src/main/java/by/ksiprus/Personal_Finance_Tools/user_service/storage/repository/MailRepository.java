package by.ksiprus.Personal_Finance_Tools.user_service.storage.repository;

import by.ksiprus.Personal_Finance_Tools.user_service.storage.entity.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MailRepository extends JpaRepository<MailEntity, UUID> {
    MailEntity findByUser_Uuid(UUID userId);

    MailEntity findByUser_Mail(String mail);

    List<MailEntity> findAllByVerifiedFalse();
}
