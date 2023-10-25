package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlot {
    @Id
    @GeneratedValue(generator = "schedule_slot_generator")
    private Long scheduleSlotId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public String toString(){
        return startDateTime.toString() + "-" + endDateTime.toString();
    }

    public boolean isIncluded(ScheduleSlot slot) {
        return !startDateTime.isAfter(slot.startDateTime) && !endDateTime.isBefore(slot.endDateTime);
    }

    public boolean startsEarlierThan(ScheduleSlot slot){
        return startDateTime.isBefore(slot.getStartDateTime());
    }

    public boolean endsLaterThan(ScheduleSlot slot){
        return endDateTime.isAfter(slot.getEndDateTime());
    }

    public boolean overlapsLoos(List<ScheduleSlot> slots) {
        ScheduleSlot earliest = slots.get(0);
        ScheduleSlot latest = slots.get(0);

        for(ScheduleSlot slot : slots){
                earliest = slot.startsEarlierThan(earliest) ? slot : earliest;
                latest = slot.endsLaterThan(latest) ? slot : latest;
        }
        return
    }
}
