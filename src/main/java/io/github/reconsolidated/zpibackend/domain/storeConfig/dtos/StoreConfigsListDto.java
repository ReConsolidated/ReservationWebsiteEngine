package io.github.reconsolidated.zpibackend.domain.storeConfig.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreConfigsListDto {
    private final List<StoreConfigDto> storeConfigList;
}
