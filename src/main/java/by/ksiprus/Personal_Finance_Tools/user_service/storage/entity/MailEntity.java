package by.ksiprus.Personal_Finance_Tools.user_service.storage.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mail_verification", schema = "finance_tool_shema")
public class MailEntity {

    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "verified", nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Column(name = "email_count", nullable = false)
    @Builder.Default
    private int emailCount = 1;

    @Column(name = "dt_create", nullable = false)
    private long dt_create;

    @Column(name = "dt_verified")
    private Long dt_verified;
}
