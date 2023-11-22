package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.availability.Availability;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemStatus;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ItemMapper {

    public ItemStatus getItemStatus(Item item) {
        Schedule schedule = item.getSchedule();
        ItemStatus itemStatus = new ItemStatus();
        itemStatus.setMark(3L);
        Availability availability = new Availability();
        itemStatus.setSchedule(availability);
        itemStatus.setAvailableAmount(Long.valueOf(item.getAmount()));
        for (ScheduleSlot slot : schedule.getAvailableScheduleSlots()) {
            if (itemStatus.getEarliestStart() == null ||
                    slot.getStartDateTime().isBefore(itemStatus.getEarliestStart())) {
                itemStatus.setEarliestStart(slot.getStartDateTime());
            }
            if (itemStatus.getLatestEnd() == null ||
                    slot.getEndDateTime().isAfter(itemStatus.getLatestEnd())) {
                itemStatus.setLatestEnd(slot.getEndDateTime());
            }
        }
        return itemStatus;
    }
}
