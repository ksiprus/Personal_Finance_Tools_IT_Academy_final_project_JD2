package by.ksiprus.Personal_Finance_Tools.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Страница
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Страница")
public abstract class BasePage {
    
    @JsonProperty("number")
    @Schema(description = "Номер текущей запрошенной страницы", format = "int32")
    private Integer number;
    
    @JsonProperty("size")
    @Schema(description = "Запрошенное количество элементов на страницу", format = "int32")
    private Integer size;
    
    @JsonProperty("total_pages")
    @Schema(description = "Всего количество страниц учитывающих запрашиваемое количество элементов на страницу", format = "int32")
    private Integer total_pages;
    
    @JsonProperty("total_elements")
    @Schema(description = "Всего количество записей соответствующих запросу", format = "int64")
    private Long total_elements;
    
    @JsonProperty("first")
    @Schema(description = "Признак является ли страница первой")
    private Boolean first;
    
    @JsonProperty("number_of_elements")
    @Schema(description = "Количество элементов на текущей странице", format = "int32")
    private Integer number_of_elements;
    
    @JsonProperty("last")
    @Schema(description = "Признак является ли страница последней")
    private Boolean last;
}