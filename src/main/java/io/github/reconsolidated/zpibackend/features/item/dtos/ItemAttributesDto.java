package io.github.reconsolidated.zpibackend.features.item.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemAttributesDto {

    private String title;
    private String subtitle;
    private String description;
    private String image;

}
