package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemListDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemDto;
import io.github.reconsolidated.zpibackend.domain.parameter.Parameter;
import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationType;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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
        if (store.getStoreConfig().getCore().getFlexibility()) {
            this.schedule = new Schedule(this, itemDto.getSchedule().getScheduledRanges());
            this.initialSchedule = new Schedule(this, itemDto.getSchedule().getScheduledRanges());
        } else {
            this.schedule = new Schedule(this,
                    List.of(new Availability(
                            itemDto.getSchedule().getStartDateTime(),
                            itemDto.getSchedule().getEndDateTime() == null ?
                                    itemDto.getSchedule().getStartDateTime() :
                                    itemDto.getSchedule().getEndDateTime(),
                            ReservationType.NONE)));
            this.initialSchedule = this.schedule;
        }
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

    public boolean isFixedPast() {
        if (store.getStoreConfig().getCore().getFlexibility()) {
            return false;
        }
        if (store.getStoreConfig().getCore().getSpecificReservation() ||
                store.getStoreConfig().getCore().getPeriodicity()) {
            return subItems.stream().anyMatch(subItem -> subItem.getStartDateTime().isBefore(LocalDateTime.now()));
        } else {
            return schedule != null &&
                    !schedule.getAvailableScheduleSlots().isEmpty() &&
                    schedule.getAvailableScheduleSlots().get(0).getStartDateTime().isBefore(LocalDateTime.now());
        }

    }
}
