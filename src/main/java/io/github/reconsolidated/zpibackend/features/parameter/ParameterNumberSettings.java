package io.github.reconsolidated.zpibackend.features.parameter;

import javax.persistence.Entity;

@Entity
public class ParameterNumberSettings extends ParameterSettings {
    private String units;
    private Integer maxValue;
    private Integer minValue;
}
