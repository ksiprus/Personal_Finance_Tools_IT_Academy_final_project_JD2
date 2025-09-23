package by.ksiprus.Personal_Finance_Tools.user_service.storage;

import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.models.User;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.api.IUserStorage;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.entity.UserEntity;
import by.ksiprus.Personal_Finance_Tools.user_service.storage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserStorage implements IUserStorage {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public boolean add(UserCreateRequest user) {
        userRepository.save(
                UserEntity.builder()
                        .uuid(user.getUuid())
                        .dt_create(user.getDt_create())
                        .dt_update(user.getDt_update())
                        .mail(user.getMail())
                        .fio(user.getFio())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .password(user.getPassword())
                        .build()
        );
        return true;
    }

    @Override
    @Transactional
    public boolean update(UUID uuid, long dt_update, UserCreateRequest user) {
        var optionalUser = userRepository.findById(uuid);
        if (optionalUser.isEmpty()) {
            return false;
        }
        
        UserEntity existing = optionalUser.get();
        existing.setDt_update(Instant.now().toEpochMilli());

        if (user.getMail() != null && !existing.getMail().equals(user.getMail())) {
            existing.setMail(user.getMail());
        }
        if (user.getFio() != null && !existing.getFio().equals(user.getFio())) {
            existing.setFio(user.getFio());
        }
        if (user.getRole() != null && !existing.getRole().equals(user.getRole())) {
            existing.setRole(user.getRole());
        }
        if (user.getStatus() != null && !existing.getStatus().equals(user.getStatus())) {
            existing.setStatus(user.getStatus());
        }
        if (user.getPassword() != null && !existing.getPassword().equals(user.getPassword())) {
            existing.setPassword(user.getPassword());
        }
        userRepository.save(existing);
        return true;
    }

    @Override
    public Page<User> get(int page, int size) {
        Page<UserEntity> entityPage = userRepository.findAll(PageRequest.of(page, size));
        return entityPage.map(entity -> User.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .mail(entity.getMail())
                .fio(entity.getFio())
                .role(entity.getRole())
                .status(entity.getStatus())
                .password(entity.getPassword())
                .build());
    }

    @Override
    public User getByUuid(UUID uuid) {
        return userRepository.findById(uuid)
                .map(entity -> User.builder()
                        .uuid(entity.getUuid())
                        .dt_create(entity.getDt_create())
                        .dt_update(entity.getDt_update())
                        .mail(entity.getMail())
                        .fio(entity.getFio())
                        .role(entity.getRole())
                        .status(entity.getStatus())
                        .password(entity.getPassword())
                        .build())
                .orElse(null);
    }

    @Override
    public User getByMail(String mail) {
        return userRepository.findByMail(mail)
                .map(entity -> User.builder()
                        .uuid(entity.getUuid())
                        .dt_create(entity.getDt_create())
                        .dt_update(entity.getDt_update())
                        .mail(entity.getMail())
                        .fio(entity.getFio())
                        .role(entity.getRole())
                        .status(entity.getStatus())
                        .password(entity.getPassword())
                        .build())
                .orElse(null);
    }

    @Override
    public User login(String mail, String password) {
        UserEntity found = userRepository.findByMail(mail).orElseThrow();
        if (!found.getPassword().equals(password)) {
            return null;
        }
        return User.builder()
                .uuid(found.getUuid())
                .dt_create(found.getDt_create())
                .dt_update(found.getDt_update())
                .mail(found.getMail())
                .fio(found.getFio())
                .role(found.getRole())
                .status(found.getStatus())
                .password(found.getPassword())
                .build();

    }
}
