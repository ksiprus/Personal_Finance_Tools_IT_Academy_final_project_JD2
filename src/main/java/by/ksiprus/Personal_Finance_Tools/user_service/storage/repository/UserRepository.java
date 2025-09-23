package by.ksiprus.Personal_Finance_Tools.user_service.storage.repository;

import by.ksiprus.Personal_Finance_Tools.user_service.storage.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByMail(String mail);
}
