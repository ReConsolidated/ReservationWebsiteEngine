package io.github.reconsolidated.zpibackend.features.item.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ItemListDto {
    private final List<ItemDto> items;
}
