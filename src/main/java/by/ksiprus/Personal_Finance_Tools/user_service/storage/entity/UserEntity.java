package by.ksiprus.Personal_Finance_Tools.user_service.storage.entity;

import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserRole;
import by.ksiprus.Personal_Finance_Tools.user_service.models.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@Table(name = "users", schema = "finance_tool_shema")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    UUID uuid;
    @Column(name = "dt_create")
    long dt_create;
    @Column(name = "dt_update")
    long dt_update;
    @Column(name = "mail")
    String mail;
    @Column(name = "fio")
    String fio;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    UserRole role;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status;
    @Column(name = "password")
    String password;
}
