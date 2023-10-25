package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private Boolean active;
    private String title;
    private String subtitle;
    private String description;
    private String image;
    private List<Parameter> customAtributeList;
    private Integer amount;
}
