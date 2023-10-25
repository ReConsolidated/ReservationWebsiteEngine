package io.github.reconsolidated.zpibackend.features.storeConfig;

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
    @GeneratedValue(generator = "store_config_generator")
    private Long storeConfigId;
    @OneToOne(cascade = CascadeType.ALL)
    private Owner owner;
    private StoreAccessType requiredStoreAccessType = StoreAccessType.ALL;
    @OneToOne(cascade = CascadeType.ALL)
    private CoreConfig core;
    @OneToOne(cascade = CascadeType.ALL)
    private MainPageConfig mainPage;
    @OneToOne(cascade = CascadeType.ALL)
    private DetailsPageConfig detailsPage;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ParameterSettings> customAttributesSpec;
}
