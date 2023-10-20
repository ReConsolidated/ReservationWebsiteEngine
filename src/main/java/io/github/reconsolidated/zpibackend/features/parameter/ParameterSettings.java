package io.github.reconsolidated.zpibackend.features.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParameterSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long parameterSettingsId;
    protected String name;
    protected ParameterType dataType;
    protected Boolean isRequired;
    protected Boolean isFilterable;
    protected Boolean showMainPage;
    protected Boolean showDetailsPage;
}
