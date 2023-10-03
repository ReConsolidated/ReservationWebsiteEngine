package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
