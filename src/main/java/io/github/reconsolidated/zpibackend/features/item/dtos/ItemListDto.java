package io.github.reconsolidated.zpibackend.features.item.dtos;

import io.github.reconsolidated.zpibackend.features.item.Item;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ItemListDto {
    private final List<Item> items;
}
