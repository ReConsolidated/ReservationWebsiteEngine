package io.github.reconsolidated.zpibackend.features.parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
public class ParameterSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected String name;
    protected ParameterType type;
    protected Boolean isRequired;
    protected Boolean isFilterable;
    protected Boolean showFirstScreen;
    protected Boolean showSecondScreen;
}
