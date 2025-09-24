package by.ksiprus.Personal_Finance_Tools.user_service.storage.api;


import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.models.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IUserStorage {
    boolean add(UserCreateRequest user);

    boolean update(UUID uuid, long dt_update, UserCreateRequest user);

    Page<User> get(int page, int size);

    User getByUuid(UUID uuid);

    User getByMail(String mail);

    // Метод login удален - логика авторизации в UserService
}
