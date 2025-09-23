package by.ksiprus.Personal_Finance_Tools.user_service.services.api;

public interface IMailerService {
    boolean verify(String mail, String code);
    void sendMail(String mail, String code);
}
