package io.github.reconsolidated.zpibackend.domain.store;

import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Store {

    @Id
    @GeneratedValue(generator = "store_generator")
    private Long id;
    private Long ownerAppUserId;
    @Column(unique = true)
    private String storeName;
    @OneToOne(cascade = CascadeType.ALL)
    private StoreConfig storeConfig;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "store")
    private List<Item> items;

    public Store(StoreConfig storeConfig) {
        this.storeName = storeConfig.getOwner().getStoreName().replaceAll("[ /]", "_");
        this.storeConfig = storeConfig;
        this.ownerAppUserId = storeConfig.getOwner().getAppUserId();
        this.items = new ArrayList<>();
    }
}
