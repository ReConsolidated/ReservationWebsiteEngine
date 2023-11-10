package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private Boolean active;
    private String title;
    private String subtitle;
    private String description;
    private String image;
    private List<Parameter> customAttributeList;
    private Integer capacity;
    private Integer quantity;

    public ItemDto(Item item) {
        this.id = item.getItemId();
        this.active = item.getActive();
        this.title = item.getTitle();
        this.subtitle = item.getSubtitle();
        this.description = item.getDescription();
        this.image = item.getImage();
        this.customAttributeList = item.getCustomAttributeList();
        this.capacity = item.getCapacity();
        this.quantity = item.getQuantity();
    }
}
