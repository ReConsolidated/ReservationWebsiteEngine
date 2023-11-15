package io.github.reconsolidated.zpibackend.features.storeConfig.dtos;

import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreConfigsListDto {
    private final List<StoreConfigDto> storeConfigList;
}
