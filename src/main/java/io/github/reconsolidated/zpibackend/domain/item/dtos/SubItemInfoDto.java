package io.github.reconsolidated.zpibackend.domain.item.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubItemInfoDto {

    private Long id;
    private String title;
    private String subtitle;
}
