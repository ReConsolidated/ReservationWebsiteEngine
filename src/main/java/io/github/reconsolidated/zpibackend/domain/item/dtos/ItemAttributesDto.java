package io.github.reconsolidated.zpibackend.domain.item.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemAttributesDto {

    private String title;
    private String subtitle;
    private String description;
    private String image;

}
