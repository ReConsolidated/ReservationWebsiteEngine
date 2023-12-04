package io.github.reconsolidated.zpibackend.domain.parameter;

import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ParameterBooleanSettings extends ParameterSettings {

    public ParameterBooleanSettings(ParameterSettings parameterSettings) {
        super(parameterSettings.getId(),
                parameterSettings.getStoreConfig(),
                parameterSettings.getName(),
                parameterSettings.getDataType(),
                parameterSettings.getIsRequired(),
                parameterSettings.getIsFilterable(),
                parameterSettings.getShowMainPage(),
                parameterSettings.getShowDetailsPage());
    }
}
