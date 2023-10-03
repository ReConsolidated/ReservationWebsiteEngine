package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreConfig {
    @Id
    @GeneratedValue(generator="store_config_generator")
    private Long storeConfigId;
    @OneToOne(cascade = CascadeType.ALL)
    private CoreConfig coreConfig;
    @OneToOne(cascade = CascadeType.ALL)
    private LayoutConfig layoutConfig;
    @OneToOne(cascade = CascadeType.ALL)
    private ItemConfig itemConfig;
}
