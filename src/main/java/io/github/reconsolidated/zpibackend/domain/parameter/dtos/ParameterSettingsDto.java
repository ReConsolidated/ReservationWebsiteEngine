package io.github.reconsolidated.zpibackend.domain.parameter.dtos;

import io.github.reconsolidated.zpibackend.domain.parameter.ParameterSettings;
import io.github.reconsolidated.zpibackend.domain.parameter.ParameterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSettingsDto {

    protected Long id;
    protected String name;
    protected ParameterType dataType;
    protected Boolean isRequired;
    protected Boolean isFilterable;
    protected Boolean showMainPage;
    protected Boolean showDetailsPage;

    public ParameterSettingsDto(ParameterSettings parameterSettings) {
        this.id = parameterSettings.getId();
        this.name = parameterSettings.getName();
        this.dataType = parameterSettings.getDataType();
        this.isRequired = parameterSettings.getIsRequired();
        this.isFilterable = parameterSettings.getIsFilterable();
        this.showMainPage = parameterSettings.getShowMainPage();
        this.showDetailsPage = parameterSettings.getShowDetailsPage();
    }
}
