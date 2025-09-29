package by.ksiprus.Personal_Finance_Tools.account.storage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity для операций на счетах
 */
@Entity
@Data
@Builder
@Table(name = "operations", schema = "finance_tool_shema")
@NoArgsConstructor
@AllArgsConstructor
public class OperationEntity {
    
    @Id
    private UUID uuid;
    
    @Column(name = "dt_create")
    private Long dt_create;
    
    @Column(name = "dt_update")
    private Long dt_update;
    
    @Column(name = "date", nullable = false)
    private Long date;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "category", nullable = false)
    private UUID category;
    
    @Column(name = "value", nullable = false, precision = 15, scale = 2)
    private BigDecimal value;
    
    @Column(name = "currency", nullable = false)
    private UUID currency;
    
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
}