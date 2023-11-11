package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.utils.BooleanListToStringConverter;
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
public class ScheduleSlot {
    @Id
    @GeneratedValue(generator = "schedule_slot_generator")
    private Long scheduleSlotId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;
    @Convert(converter = BooleanListToStringConverter.class)
    private List<Boolean> itemsAvailability;
    private ReservationType type = ReservationType.NONE;

    public ScheduleSlot(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer amount) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.amount = amount;
        this.itemsAvailability = new ArrayList<>(amount);
        for(int i = 0; i < amount; i++) {
            itemsAvailability.add(true);
        }
    }

    public ScheduleSlot(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer amount, ReservationType type) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
        this.amount = amount;
        this.itemsAvailability = new ArrayList<>(amount);
        for(int i = 0; i < amount; i++) {
            itemsAvailability.add(true);
        }
    }

    public String toString(){
        return startDateTime.toString() + "-" + endDateTime.toString() + ": " + this.amount;
    }

    public ScheduleSlot[] split(LocalDateTime splitBy) {
        if(!startDateTime.isBefore(splitBy) || !endDateTime.isAfter(splitBy)) {
            throw new IllegalArgumentException();
        }
        ScheduleSlot first = new ScheduleSlot(startDateTime, splitBy, itemsAvailability.size());
        ScheduleSlot second = new ScheduleSlot(splitBy, endDateTime, itemsAvailability.size());
        for(int i = 0; i < itemsAvailability.size(); i++) {
            first.getItemsAvailability().set(i, itemsAvailability.get(i));
            second.getItemsAvailability().set(i, itemsAvailability.get(i));
        }
        first.setAmount(amount);
        second.setAmount(amount);

        return new ScheduleSlot[]{first, second};
    }

    public boolean equalsTime(ScheduleSlot scheduleSlot){
        return startDateTime.equals(scheduleSlot.startDateTime) && endDateTime.equals(scheduleSlot.endDateTime);
    }

    public boolean equalsTimeFitAmount(ScheduleSlot scheduleSlot){
        return startDateTime.equals(scheduleSlot.startDateTime) &&
                endDateTime.equals(scheduleSlot.endDateTime) &&
                amount >= scheduleSlot.amount;
    }

    public boolean isIncluded(ScheduleSlot slot) {
        return !startDateTime.isAfter(slot.startDateTime) && !endDateTime.isBefore(slot.endDateTime);
    }

    public ScheduleSlot marge(ScheduleSlot scheduleSlot) {
        if(scheduleSlot.startDateTime.equals(startDateTime) && scheduleSlot.endDateTime.equals(endDateTime)) {
            amount += scheduleSlot.amount;
            return this;
        } else {
            throw new IllegalArgumentException();
        }
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleSlot that)) return false;

        if (!amount.equals(that.amount)) return false;
        if (!itemsAvailability.equals(that.itemsAvailability)) return false;
//        if (itemsAvailability.size() != that.itemsAvailability.size()) return false;
//        for(int i = 0; i < itemsAvailability.size(); i++) {
//            if (itemsAvailability.get(i) != that.itemsAvailability.get(i)) {
//                return false;
//            }
//        }
        if (!startDateTime.equals(that.startDateTime)) return false;
        return endDateTime.equals(that.endDateTime);
    }

    @Override
    public int hashCode() {
        int result = startDateTime.hashCode();
        result = 31 * result + endDateTime.hashCode();
        return result;
    }
}
