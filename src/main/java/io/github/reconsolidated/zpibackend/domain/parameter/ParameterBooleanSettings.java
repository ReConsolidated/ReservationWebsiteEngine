package io.github.reconsolidated.zpibackend.domain.parameter;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ParameterBooleanSettings extends ParameterSettings {

    public ParameterBooleanSettings(ParameterSettings parameterSettings) {
        super(parameterSettings.getId(),
                parameterSettings.getName(),
                parameterSettings.getDataType(),
                parameterSettings.getIsRequired(),
                parameterSettings.getIsFilterable(),
                parameterSettings.getShowMainPage(),
                parameterSettings.getShowDetailsPage());
    }
}
