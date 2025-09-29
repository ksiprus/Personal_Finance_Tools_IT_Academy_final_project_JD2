package by.ksiprus.Personal_Finance_Tools.account.storage.entity;

import by.ksiprus.Personal_Finance_Tools.account.models.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity для счетов пользователя
 */
@Entity
@Data
@Builder
@Table(name = "accounts", schema = "finance_tool_shema")
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    
    @Id
    private UUID uuid;
    
    @Column(name = "dt_create")
    private Long dt_create;
    
    @Column(name = "dt_update")
    private Long dt_update;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;
    
    @Column(name = "currency", nullable = false)
    private UUID currency;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
}