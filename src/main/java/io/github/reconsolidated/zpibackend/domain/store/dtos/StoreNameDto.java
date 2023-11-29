package io.github.reconsolidated.zpibackend.domain.store.dtos;

import io.github.reconsolidated.zpibackend.domain.store.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreNameDto {
    private String storeName;
    private String storeConfigId;

    public StoreNameDto(Store store) {
        this.storeName = store.getStoreConfig().getOwner().getStoreName();
        this.storeConfigId = store.getStoreName();
    }
}
