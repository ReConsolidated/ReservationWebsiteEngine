package io.github.reconsolidated.zpibackend.features.item.dtos;

import io.github.reconsolidated.zpibackend.features.availability.Availability;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.SubItem;
import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private static final double AVERAGE = 3.0;
    private static final int LATEST_HOUR = 23;

    private Long id;
    private Boolean active;
    private ItemAttributesDto attributesDto;
    private List<Parameter> customAttributeList = new ArrayList<>();
    private List<SubItem> subItems = new ArrayList<>();
    private List<Availability> availabilities = new ArrayList<>();
    private Integer amount;
    private Integer availableAmount = amount;
    private Double mark;
    private Integer earliestStartHour = LATEST_HOUR;
    private Integer latestEndHour = 0;

    public ItemDto(Item item) {
        this.id = item.getItemId();
        this.active = item.getActive();
        this.attributesDto = new ItemAttributesDto(
                item.getTitle(),
                item.getSubtitle(),
                item.getDescription(),
                item.getImage());
        this.customAttributeList = item.getCustomAttributeList();
        this.amount = item.getAmount();
        this.availableAmount = item.getAmount();
        this.mark = AVERAGE;
        this.subItems = item.getSubItems();
        this.availabilities = item.getSchedule().getAvailabilities();

        for (ScheduleSlot slot : item.getSchedule().getAvailableScheduleSlots()) {
            if (earliestStartHour > slot.getStartDateTime().getHour()) {
                earliestStartHour = slot.getStartDateTime().getHour();
            }
            if (latestEndHour < slot.getEndDateTime().getHour()) {
                latestEndHour = slot.getEndDateTime().getHour();
            }
        }
    }
}
