package io.github.reconsolidated.zpibackend.domain.storeConfig;

import io.github.reconsolidated.zpibackend.domain.parameter.ParameterSettings;
import io.github.reconsolidated.zpibackend.domain.store.dtos.StoreNameDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreConfig {
    @Id
    @GeneratedValue(generator = "store_config_generator")
    private Long storeConfigId;
    @OneToOne(cascade = CascadeType.ALL)
    private Owner owner;
    @OneToOne(cascade = CascadeType.ALL)
    private CoreConfig core;
    @OneToOne(cascade = CascadeType.ALL)
    private MainPageConfig mainPage;
    @OneToOne(cascade = CascadeType.ALL)
    private DetailsPageConfig detailsPage;
    @OneToOne(cascade = CascadeType.ALL)
    private AuthenticationConfig authConfig;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "storeConfig")
    private List<ParameterSettings> customAttributesSpec;

    public StoreConfig(Owner owner, CoreConfig core, MainPageConfig mainPage, DetailsPageConfig detailsPage,
                       AuthenticationConfig authConfig, List<ParameterSettings> customAttributesSpec) {
        this.owner = owner;
        this.core = core;
        this.mainPage = mainPage;
        this.detailsPage = detailsPage;
        this.authConfig = authConfig;
        this.customAttributesSpec = customAttributesSpec;
        this.customAttributesSpec.forEach(parameterSettings -> parameterSettings.setStoreConfig(this));
    }

    public StoreNameDto getStoreSummary() {
        return new StoreNameDto(this);
    }
}
