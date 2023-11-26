package io.github.reconsolidated.zpibackend.features.store.dtos;

import io.github.reconsolidated.zpibackend.features.store.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreNameDto {
    private String storeName;
    private Long id;

    public StoreNameDto(Store store) {
        this.storeName = store.getStoreName();
        this.id = store.getId();
    }
}
