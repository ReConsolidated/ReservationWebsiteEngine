package io.github.reconsolidated.zpibackend.features.parameter;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ParameterNumberSettings extends ParameterSettings {
    private String units;
    private Integer maxValue;
    private Integer minValue;

    public ParameterNumberSettings(ParameterSettings parameterSettings, String units, Integer maxValue,
                                   Integer minValue) {
        super(parameterSettings.getId(),
                parameterSettings.getName(),
                parameterSettings.getDataType(),
                parameterSettings.getIsRequired(),
                parameterSettings.getIsFilterable(),
                parameterSettings.getShowMainPage(),
                parameterSettings.getShowDetailsPage());
    }
}
