package by.ksiprus.Personal_Finance_Tools.user_service.services.api;

import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserCreateRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.request.UserRegistrationRequest;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.LoginResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.dto.response.UserResponse;
import by.ksiprus.Personal_Finance_Tools.user_service.models.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IUserService {
    boolean create(UserCreateRequest userCreateRequest);
    boolean create(UserRegistrationRequest userRegistrationRequest);
    boolean update( UUID uuid, long dt_update, UserCreateRequest userCreateRequest);
    Page<UserResponse> get(int page, int size);
    UserResponse getByUuid(UUID uuid);
    User getByMail(String mail);
    LoginResponse login(String mail, String password);
}