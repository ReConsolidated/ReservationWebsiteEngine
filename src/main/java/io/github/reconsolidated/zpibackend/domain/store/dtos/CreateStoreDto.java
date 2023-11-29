package io.github.reconsolidated.zpibackend.domain.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateStoreDto {
    private Long storeConfigId;
    private String storeName;
}
