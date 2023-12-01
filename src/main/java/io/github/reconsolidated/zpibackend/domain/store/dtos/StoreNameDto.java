package io.github.reconsolidated.zpibackend.domain.store.dtos;

import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreNameDto {
    private String name;
    private String storeConfigId;

    public StoreNameDto(StoreConfig storeConfig) {
        this.name = storeConfig.getOwner().getStoreName();
        this.storeConfigId = storeConfig.getName();
    }
}
