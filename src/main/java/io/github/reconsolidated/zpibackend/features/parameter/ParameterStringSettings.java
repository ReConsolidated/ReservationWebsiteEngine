package io.github.reconsolidated.zpibackend.features.parameter;

import lombok.*;

import javax.persistence.DiscriminatorValue;
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
}
