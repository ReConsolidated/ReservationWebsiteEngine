package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Getter
@Entity
public class StoreConfig {
    @Id
    @GeneratedValue(generator="store_config_generator")
    private Long storeConfigId;
    @OneToOne
    private CoreConfig coreConfig;
    @OneToOne
    private LayoutConfig layoutConfig;
    @OneToOne
    private ItemConfig itemConfig;
}
