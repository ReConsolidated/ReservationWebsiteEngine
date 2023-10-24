package io.github.reconsolidated.zpibackend.features.parameter;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ParameterBooleanSettings extends ParameterSettings {

    public ParameterBooleanSettings(ParameterSettings parameterSettings) {
        super(null,
                parameterSettings.getName(),
                parameterSettings.getDataType(),
                parameterSettings.getIsRequired(),
                parameterSettings.getIsFilterable(),
                parameterSettings.getShowMainPage(),
                parameterSettings.getShowDetailsPage());
    }
}
