package by.ksiprus.Personal_Finance_Tools.account.utils;

import by.ksiprus.Personal_Finance_Tools.account.models.Account;
import by.ksiprus.Personal_Finance_Tools.account.models.Operation;
import by.ksiprus.Personal_Finance_Tools.account.storage.entity.AccountEntity;
import by.ksiprus.Personal_Finance_Tools.account.storage.entity.OperationEntity;

/**
 * Утилиты для маппинга между сущностями и моделями в сервисе счетов.
 */
public final class AccountMapperUtils {
    
    /**
     * Преобразует AccountEntity в Account.
     */
    public static Account mapAccountEntityToModel(AccountEntity entity) {
        return Account.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .balance(entity.getBalance())
                .type(entity.getType())
                .currency(entity.getCurrency())
                .build();
    }
    
    /**
     * Преобразует OperationEntity в Operation.
     */
    public static Operation mapOperationEntityToModel(OperationEntity entity) {
        return Operation.builder()
                .uuid(entity.getUuid())
                .dt_create(entity.getDt_create())
                .dt_update(entity.getDt_update())
                .date(entity.getDate())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .value(entity.getValue())
                .currency(entity.getCurrency())
                .build();
    }
    
    private AccountMapperUtils() {
        // Utility class - prevent instantiation
    }
}