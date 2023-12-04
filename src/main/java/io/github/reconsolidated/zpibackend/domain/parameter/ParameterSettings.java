package io.github.reconsolidated.zpibackend.domain.parameter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.reconsolidated.zpibackend.domain.parameter.dtos.ParameterSettingsDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
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
    protected Long id;
    @JoinColumn(name = "store_config_id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    protected StoreConfig storeConfig;
    protected String name;
    protected ParameterType dataType;
    protected Boolean isRequired;
    protected Boolean isFilterable;
    protected Boolean showMainPage;
    protected Boolean showDetailsPage;

    public ParameterSettings(ParameterSettingsDto parameterSettingsDto) {
        this.id = parameterSettingsDto.getId();
        this.name = parameterSettingsDto.getName();
        this.dataType = parameterSettingsDto.getDataType();
        this.isRequired = parameterSettingsDto.getIsRequired();
        this.isFilterable = parameterSettingsDto.getIsFilterable();
        this.showMainPage = parameterSettingsDto.getShowMainPage();
        this.showDetailsPage = parameterSettingsDto.getShowDetailsPage();
    }
}
