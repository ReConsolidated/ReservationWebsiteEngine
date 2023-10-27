package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlot {
    @Id
    @GeneratedValue(generator = "schedule_slot_generator")
    private Long scheduleSlotId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;
    private Integer capacity;
    private ReservationType type = ReservationType.NONE;

    public String toString(){
        return startDateTime.toString() + "-" + endDateTime.toString();
    }

    public boolean equalsTime(ScheduleSlot scheduleSlot){
        return startDateTime.equals(scheduleSlot.startDateTime) && endDateTime.equals(scheduleSlot.endDateTime);
    }

    public boolean equalsTimeFit(ScheduleSlot scheduleSlot){
        return startDateTime.equals(scheduleSlot.startDateTime) &&
                endDateTime.equals(scheduleSlot.endDateTime) &&
                amount >= scheduleSlot.amount &&
                capacity >= scheduleSlot.capacity;
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

//    public boolean overlapsLoos(List<ScheduleSlot> slots) {
//
//        if(slots.isEmpty()) {
//            return false;
//        }
//
//        ScheduleSlot earliest = slots.get(0);
//        ScheduleSlot latest = slots.get(0);
//
//        for(ScheduleSlot slot : slots){
//                earliest = slot.startsEarlierThan(earliest) ? slot : earliest;
//                latest = slot.endsLaterThan(latest) ? slot : latest;
//        }
//
//        return !startDateTime.isAfter(earliest.startDateTime) && !endDateTime.isBefore(latest.endDateTime);
//    }

//    public boolean overlaps(List<ScheduleSlot> slots) {
//
//        if(slots.isEmpty()) {
//            return false;
//        }
//
//        slots.sort(Comparator.comparing((ScheduleSlot slot) -> slot.startDateTime));
//
//        boolean overlaps = false;
//        LocalDateTime prevSlotEndTime = slots.get(0).startDateTime;
//
//        if(!prevSlotEndTime.isAfter(startDateTime)) {
//            Iterator<ScheduleSlot> iterator = slots.listIterator();
//
//            while (iterator.hasNext()) {
//                ScheduleSlot currSlot = iterator.next();
//                if(currSlot.startDateTime.isAfter(prevSlotEndTime)) {
//                    break;
//                }
//                if(currSlot.getAmount() < amount || currSlot.getCapacity() < capacity)
//                if(currSlot.endDateTime.isAfter(endDateTime)) {
//                    overlaps = true;
//                    break;
//                }
//            }
//        }
//
//        return overlaps;
//    }

}
