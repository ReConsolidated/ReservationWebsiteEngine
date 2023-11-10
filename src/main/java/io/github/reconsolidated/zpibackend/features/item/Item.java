package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.item.dtos.ItemStatus;
import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import io.github.reconsolidated.zpibackend.features.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long itemId;
    @ManyToOne
    private Store store;
    private Boolean active;
    private String title;
    private String subtitle;
    private String description;
    private String image;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Parameter> customAttributeList;
    private Integer capacity;
    private Integer quantity;
    private LocalDateTime rentalStart;
    private LocalDateTime rentalEnd;

    public Item(Store store, ItemDto itemDto) {
        this.store = store;
        this.active = itemDto.getActive();
        this.title = itemDto.getTitle();
        this.subtitle = itemDto.getSubtitle();
        this.description = itemDto.getDescription();
        this.image = itemDto.getImage();
        this.capacity = itemDto.getCapacity();
        this.quantity = itemDto.getQuantity();
        this.customAttributeList = itemDto.getCustomAttributeList();
    }

}
