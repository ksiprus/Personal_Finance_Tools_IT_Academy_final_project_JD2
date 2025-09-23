package by.ksiprus.Personal_Finance_Tools.utils;

import by.ksiprus.Personal_Finance_Tools.utils.api.ICodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGenerator implements ICodeGenerator {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();
    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int idx = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(idx));
        }
        return sb.toString();
    }
}