package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.features.parameter.ParameterSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreConfig {
    @Id
    @GeneratedValue(generator = "store_config_generator")
    private Long storeConfigId;
    @OneToOne
    private Owner owner;
    @OneToOne(cascade = CascadeType.ALL)
    private CoreConfig coreConfig;
    @OneToOne(cascade = CascadeType.ALL)
    private MainPageConfig mainPageConfig;
    @OneToOne(cascade = CascadeType.ALL)
    private DetailsPageConfig detailsPageConfig;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ParameterSettings> parameterMap;
}
