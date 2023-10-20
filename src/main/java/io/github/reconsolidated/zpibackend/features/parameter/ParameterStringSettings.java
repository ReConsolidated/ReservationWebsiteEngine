package io.github.reconsolidated.zpibackend.features.parameter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class ParameterStringSettings extends ParameterSettings {
    protected Boolean limitValues;
    @ElementCollection
    protected List<String> possibleValues;
}
