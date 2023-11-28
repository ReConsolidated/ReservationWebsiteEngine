package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.features.item.dtos.SubItemDto;
import io.github.reconsolidated.zpibackend.features.item.dtos.SubItemListDto;
import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import io.github.reconsolidated.zpibackend.features.reservation.Reservation;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import io.github.reconsolidated.zpibackend.features.store.Store;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(generator = "item_generator")
    private Long itemId;
    @ManyToOne
    private Store store;
    private Boolean active;
    private String title;
    private String subtitle;
    @Column(length = 1000)
    private String description;
    private String image;
    @OneToOne(cascade = CascadeType.ALL)
    private Schedule schedule = new Schedule(this, new ArrayList<>());
    @OneToOne(cascade = CascadeType.ALL)
    private Schedule initialSchedule = new Schedule(this, new ArrayList<>());
    @OneToMany(cascade = CascadeType.ALL)
    private List<Parameter> customAttributeList;
    private Integer amount = 1;
    private Integer initialAmount = 1;
    @OneToMany(cascade = CascadeType.ALL)
    private List<SubItem> subItems;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public Item(Store store, ItemDto itemDto) {
        this.store = store;
        this.active = itemDto.getActive();
        this.title = itemDto.getAttributes().getTitle();
        this.subtitle = itemDto.getAttributes().getSubtitle();
        this.description = itemDto.getAttributes().getDescription();
        this.image = itemDto.getAttributes().getImage();
        this.amount = itemDto.getAmount();
        this.initialAmount = itemDto.getAmount();
        itemDto.getCustomAttributeList().forEach(attribute -> attribute.setId(null));
        this.customAttributeList = itemDto.getCustomAttributeList();
        this.schedule = new Schedule(this, itemDto.getSchedule().getScheduledRanges());
        this.initialSchedule = new Schedule(this, itemDto.getSchedule().getScheduledRanges());
        this.subItems = itemDto.getSubItems();
        this.reservations = new ArrayList<>();
    }

    public SubItemListDto getSubItemsListDto() {
        ArrayList<SubItemDto> subItemsDto = new ArrayList<>();
        for (SubItem subItem : subItems) {
            subItemsDto.add(subItem.toSubItemDto());
        }
        return new SubItemListDto(subItemsDto);
    }

    public SubItemDto toSubItemDto() {
        return new SubItemDto(itemId, title, subtitle);
    }

}
