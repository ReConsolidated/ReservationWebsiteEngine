package io.github.reconsolidated.zpibackend.features.parameter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = ParameterSettingsDeserializer.class)
public class ParameterSettings {
    @Id
    @GeneratedValue(generator = "parameter_settings_generator")
    protected Long parameterSettingsId;
    protected String name;
    protected ParameterType dataType;
    protected Boolean isRequired;
    protected Boolean isFilterable;
    protected Boolean showMainPage;
    protected Boolean showDetailsPage;
}
