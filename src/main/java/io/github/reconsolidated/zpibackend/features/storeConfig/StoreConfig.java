package io.github.reconsolidated.zpibackend.features.storeConfig;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.reconsolidated.zpibackend.features.parameter.ParameterSettings;
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
    @JsonDeserialize(as = Long.class)
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
    @OneToMany(cascade = CascadeType.ALL)
    private List<ParameterSettings> customAttributesSpec;
    @OneToOne(cascade = CascadeType.ALL)
    private AuthenticationConfig authConfig;
    private String name;

    public StoreConfig(Owner owner, CoreConfig core, MainPageConfig mainPage, DetailsPageConfig detailsPage,
                       AuthenticationConfig authConfig, List<ParameterSettings> customAttributesSpec) {
        this.owner = owner;
        this.core = core;
        this.mainPage = mainPage;
        this.detailsPage = detailsPage;
        this.authConfig = authConfig;
        this.customAttributesSpec = customAttributesSpec;
        this.name = owner.getStoreName().replaceAll("[ /]", "_");
    }

    public StoreSummary getStoreSummary() {
        return new StoreSummary(name, owner.getStoreName());
    }
}
