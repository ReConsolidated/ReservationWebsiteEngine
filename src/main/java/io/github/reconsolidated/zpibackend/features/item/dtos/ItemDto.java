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

import javax.validation.constraints.NotNull;
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
    private ItemAttributesDto attributes;
    @NotNull
    private List<Parameter> customAttributeList = new ArrayList<>();
    @NotNull
    private List<SubItem> subItems = new ArrayList<>();
    private ScheduleDto schedule;
    @NotNull
    private List<Availability> availabilities = new ArrayList<>();
    @NotNull
    private Integer amount = 1;
    private Integer availableAmount = amount;
    @NotNull
    private Double mark = 0.0;
    private Integer earliestStartHour = LATEST_HOUR;
    private Integer latestEndHour = 0;

    public ItemDto(Item item) {
        this.id = item.getItemId();
        this.active = item.getActive();
        this.attributes = new ItemAttributesDto(
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
        if(item.getStore().getStoreConfig().getCore().getFlexibility()) {
            this.schedule = new ScheduleDto(item.getInitialSchedule().getAvailabilities(), null, null);
        } else {
            Availability single = item.getInitialSchedule().getAvailabilities().get(0);
            this.schedule = new ScheduleDto(single.getStartDateTime(), single.getEndDateTime());
        }

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
