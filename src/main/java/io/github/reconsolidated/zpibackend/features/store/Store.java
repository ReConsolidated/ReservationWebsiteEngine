package io.github.reconsolidated.zpibackend.features.store;

import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
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
    private String storeName;
    @OneToOne
    private StoreConfig storeConfig;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Item> items;

    public Store(String storeName, StoreConfig storeConfig) {
        this.storeName = storeName;
        this.storeConfig = storeConfig;
        this.ownerAppUserId = storeConfig.getOwner().getAppUserId();
        this.items = new ArrayList<>();
    }
}
