package by.ksiprus.Personal_Finance_Tools.classifier_service.storage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "currency", schema = "classifier")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyEntity {
    
    @Id
    private UUID uuid;
    
    @Column(name = "dt_create", nullable = false)
    private Long dt_create;
    
    @Column(name = "dt_update", nullable = false)
    private Long dt_update;
    
    @Column(name = "title", nullable = false, unique = true, length = 10)
    private String title;
    
    @Column(name = "description", length = 255)
    private String description;
}