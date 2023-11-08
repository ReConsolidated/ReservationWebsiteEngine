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

}
