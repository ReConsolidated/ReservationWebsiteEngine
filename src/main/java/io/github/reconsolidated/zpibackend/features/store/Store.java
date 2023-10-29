package io.github.reconsolidated.zpibackend.features.store;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Store {

    @Id
    @GeneratedValue(generator = "store_generator")
    private Long storeId;
    @OneToOne
    private StoreConfig storeConfig;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Item> items;
    @OneToMany(cascade = CascadeType.ALL)
    private List<AppUser> addedUsers;

}
