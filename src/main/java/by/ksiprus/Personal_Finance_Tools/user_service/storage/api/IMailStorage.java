package by.ksiprus.Personal_Finance_Tools.user_service.storage.api;

import org.springframework.data.util.Pair;

import java.util.List;

public interface IMailStorage {
    boolean add(String mail, String code);

    boolean verify(String mail);

    String getCode(String mail);

    List<Pair<String, String>> getUnverifiedMailsAndCodes();

    void incrementVerifiedMailCount(String mail);
}
