package by.ksiprus.Personal_Finance_Tools.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Описание базовой сущности
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Описание базовой сущности")
public abstract class BaseEssence {
    
    @JsonProperty("uuid")
    @Schema(description = "Уникальный идентификатор сущности", 
            format = "uuid", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID uuid;
    
    @JsonProperty("dt_create")
    @Schema(description = "Дата создания сущности (linux time)", 
            format = "int64", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long dt_create;
    
    @JsonProperty("dt_update")
    @Schema(description = "Дата последнего обновления сущности (linux time)", 
            format = "int64", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long dt_update;
}