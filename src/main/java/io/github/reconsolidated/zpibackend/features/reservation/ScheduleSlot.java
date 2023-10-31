package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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

    public ScheduleSlot[] split(LocalDateTime splitBy) {
        if(!startDateTime.isBefore(splitBy) || !endDateTime.isAfter(splitBy)) {
            throw new IllegalArgumentException();
        }
        ScheduleSlot first = ScheduleSlot.builder()
                .startDateTime(startDateTime)
                .endDateTime(splitBy)
                .capacity(capacity)
                .amount(amount)
                .build();
        ScheduleSlot second = ScheduleSlot.builder()
                .startDateTime(splitBy)
                .endDateTime(endDateTime)
                .capacity(capacity)
                .amount(amount)
                .build();
        return new ScheduleSlot[]{first, second};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleSlot that)) return false;

        if (!startDateTime.equals(that.startDateTime)) return false;
        return endDateTime.equals(that.endDateTime);
    }

    @Override
    public int hashCode() {
        int result = startDateTime.hashCode();
        result = 31 * result + endDateTime.hashCode();
        return result;
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

    public boolean overlap(ScheduleSlot slots) {
        return startDateTime.isBefore(slots.endDateTime) && endDateTime.isAfter(slots.startDateTime);
    }

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
