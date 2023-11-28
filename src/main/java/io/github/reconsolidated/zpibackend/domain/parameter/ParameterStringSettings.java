package io.github.reconsolidated.zpibackend.domain.parameter;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ParameterStringSettings extends ParameterSettings {
    protected Boolean limitValues;
    @ElementCollection
    protected List<String> possibleValues;

    public ParameterStringSettings(ParameterSettings parameterSettings, Boolean limitValues,
                                   List<String> possibleValues) {
        super(parameterSettings.getId(),
                parameterSettings.getName(),
                parameterSettings.getDataType(),
                parameterSettings.getIsRequired(),
                parameterSettings.getIsFilterable(),
                parameterSettings.getShowMainPage(),
                parameterSettings.getShowDetailsPage());
        this.limitValues = limitValues;
        this.possibleValues = possibleValues;
    }
}
