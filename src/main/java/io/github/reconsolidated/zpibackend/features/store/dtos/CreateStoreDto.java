package io.github.reconsolidated.zpibackend.features.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateStoreDto {
    private Long storeConfigId;
    private String storeName;
}
