package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemListDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemInfoDto;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<Parameter> customAttributeList;
    @Builder.Default
    private Integer amount = 1;
    @Builder.Default
    private Integer initialAmount = 1;
    @OneToMany(cascade = CascadeType.ALL)
    private List<SubItem> subItems;
    @Builder.Default
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
        this.customAttributeList = itemDto.getCustomAttributeList().stream().map((p) -> new Parameter(p, this)).toList();

        if (store.getStoreConfig().getCore().getFlexibility()) {
            this.schedule = new Schedule(this, itemDto.getSchedule().getScheduledRanges());
            this.initialSchedule = new Schedule(this, itemDto.getSchedule().getScheduledRanges());
            initialSchedule.setAvailableScheduleSlots(initialSchedule
                            .getAvailableScheduleSlots()
                            .stream()
                            .filter(scheduleSlot ->
                            scheduleSlot.getType() != ReservationType.MORNING &&
                                    scheduleSlot.getType() != ReservationType.OVERNIGHT)
                            .toList());
        } else {
            this.schedule = new Schedule(this,
                    List.of(new Availability(
                            itemDto.getSchedule().getStartDateTime(),
                            itemDto.getSchedule().getEndDateTime() == null ?
                                    itemDto.getSchedule().getStartDateTime() :
                                    itemDto.getSchedule().getEndDateTime(),
                            ReservationType.NONE)));
            this.initialSchedule = new Schedule(this,
                    List.of(new Availability(
                            itemDto.getSchedule().getStartDateTime(),
                            itemDto.getSchedule().getEndDateTime() == null ?
                                    itemDto.getSchedule().getStartDateTime() :
                                    itemDto.getSchedule().getEndDateTime(),
                            ReservationType.NONE)));
            this.initialSchedule.setAvailableScheduleSlots(
                    initialSchedule.getAvailableScheduleSlots());
        }
        this.subItems = itemDto.getSubItems().stream().map(SubItem::new).toList();
        this.reservations = new ArrayList<>();
    }

    public SubItemListDto getSubItemsListDto() {
        ArrayList<SubItemInfoDto> subItemsDto = new ArrayList<>();
        for (SubItem subItem : subItems) {
            subItemsDto.add(subItem.toSubItemInfoDto());
        }
        return new SubItemListDto(subItemsDto);
    }

    public SubItemInfoDto toSubItemDto() {
        return new SubItemInfoDto(itemId, title, subtitle);
    }

    public boolean isFixedPast() {
        if (store.getStoreConfig().getCore().getFlexibility()) {
            return false;
        }
        if (store.getStoreConfig().getCore().getPeriodicity()) {
            return subItems.stream().noneMatch(subItem -> subItem.getStartDateTime().isAfter(LocalDateTime.now()));
        } else {
            return schedule != null &&
                    !schedule.getAvailableScheduleSlots().isEmpty() &&
                    schedule.getAvailableScheduleSlots().get(0).getStartDateTime().isBefore(LocalDateTime.now());
        }
    }
}
