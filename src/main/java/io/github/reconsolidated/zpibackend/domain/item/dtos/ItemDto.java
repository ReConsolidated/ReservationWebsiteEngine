package io.github.reconsolidated.zpibackend.domain.item.dtos;

import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.item.SubItem;
import io.github.reconsolidated.zpibackend.domain.parameter.Parameter;
import io.github.reconsolidated.zpibackend.domain.parameter.dtos.ParameterDto;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private static final int LATEST_HOUR = 23;

    private Long id;
    private Boolean active;
    private ItemAttributesDto attributes;
    private List<ParameterDto> customAttributeList = new ArrayList<>();
    private List<SubItemDto> subItems = new ArrayList<>();
    private ScheduleDto schedule;
    private List<Availability> availabilities = new ArrayList<>();
    private Integer amount = 1;
    private Integer availableAmount = amount;
    private Double mark = 0.0;
    private Integer ratingCount = 0;
    private Integer earliestStartHour = LATEST_HOUR;
    private Integer latestEndHour = 0;

    public ItemDto(Item item, Double average, Integer ratingCount) {
        this.id = item.getItemId();
        this.active = item.getActive();
        this.attributes = new ItemAttributesDto(
                item.getTitle(),
                item.getSubtitle(),
                item.getDescription(),
                item.getImage());
        this.customAttributeList = item.getCustomAttributeList().stream().map(ParameterDto::new).toList();
        this.amount = item.getInitialAmount();
        this.availableAmount = item.getAmount();
        this.subItems = item.getSubItems().stream().map(SubItem::toSubItemDto).toList();
        this.availabilities = item.getSchedule().getLongestAvailabilities();
        this.mark = average;
        this.ratingCount = ratingCount;
        if (item.getStore().getStoreConfig().getCore().getFlexibility()) {
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
