package io.github.reconsolidated.zpibackend.features.reservation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

    private static final int HASHCODE_CONSTANT = 31;
    @Id
    @JsonDeserialize(as = Long.class)
    @GeneratedValue(generator = "schedule_slot_generator")
    private Long scheduleSlotId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer currAmount;
    @Convert(converter = BooleanListToStringConverter.class)
    private List<Boolean> itemsAvailability;
    private ReservationType type = ReservationType.NONE;

    public ScheduleSlot(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer initialAmount) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.currAmount = initialAmount;
        this.itemsAvailability = new ArrayList<>(initialAmount);
        for (int i = 0; i < initialAmount; i++) {
            itemsAvailability.add(true);
        }
    }

    public ScheduleSlot(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer initAmount, ReservationType type) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
        this.currAmount = initAmount;
        this.itemsAvailability = new ArrayList<>(initAmount);
        for (int i = 0; i < initAmount; i++) {
            itemsAvailability.add(true);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (Boolean itemAvail: itemsAvailability) {
            stringBuilder.append(itemAvail.toString());
            stringBuilder.append(" ");
        }
        stringBuilder.append("]");
        return startDateTime.toString() + "-" + endDateTime.toString() + ": " + this.currAmount + stringBuilder.toString();
    }

    public ScheduleSlot[] split(LocalDateTime splitBy) {
        if (!startDateTime.isBefore(splitBy) || !endDateTime.isAfter(splitBy)) {
            throw new IllegalArgumentException("Cannot split Schedule slot by time outside the slot time range!\nSlot: "
                    + this.toString() + ", splitBy: " + splitBy);
        }
        ScheduleSlot first = new ScheduleSlot(startDateTime, splitBy, itemsAvailability.size());
        ScheduleSlot second = new ScheduleSlot(splitBy, endDateTime, itemsAvailability.size());
        for (int i = 0; i < itemsAvailability.size(); i++) {
            first.getItemsAvailability().set(i, itemsAvailability.get(i));
            second.getItemsAvailability().set(i, itemsAvailability.get(i));
        }
        first.setCurrAmount(currAmount);
        second.setCurrAmount(currAmount);

        return new ScheduleSlot[]{first, second};
    }

    public boolean equalsTime(ScheduleSlot scheduleSlot) {
        return startDateTime.equals(scheduleSlot.startDateTime) && endDateTime.equals(scheduleSlot.endDateTime);
    }

    public ArrayList<Integer> getAvailableItemsIndexes() {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < itemsAvailability.size(); i++) {
            if (itemsAvailability.get(i)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    public void setItemsAvailability(int initAmount, ArrayList<Integer> availableItemsIndexes) {

        List<Boolean> itemAvailability = new ArrayList<>(initAmount);
        for (int i = 0; i < initAmount; i++) {
            itemAvailability.add(availableItemsIndexes.contains(i));
        }
        this.itemsAvailability = itemAvailability;
    }

    /**
     This method check if passed scheduleSlot is continuation of this slot
     [this.end == slot.start]
     */
    public boolean isContinuousWith(ScheduleSlot scheduleSlot) {
        return endDateTime.equals(scheduleSlot.startDateTime);
    }

    public boolean equalsTimeFitAmount(ScheduleSlot scheduleSlot) {
        return startDateTime.equals(scheduleSlot.startDateTime) &&
                endDateTime.equals(scheduleSlot.endDateTime) &&
                currAmount >= scheduleSlot.currAmount;
    }

    public boolean isIncluded(ScheduleSlot slot) {
        return !startDateTime.isAfter(slot.startDateTime) && !endDateTime.isBefore(slot.endDateTime);
    }

    public boolean startsEarlierThan(ScheduleSlot slot) {
        return startDateTime.isBefore(slot.getStartDateTime());
    }

    public boolean endsLaterThan(ScheduleSlot slot) {
        return endDateTime.isAfter(slot.getEndDateTime());
    }

    public boolean overlap(ScheduleSlot slots) {
        return startDateTime.isBefore(slots.endDateTime) && endDateTime.isAfter(slots.startDateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleSlot that)) {
            return false;
        }
        if (!currAmount.equals(that.currAmount)) {
            return false;
        }
        if (!itemsAvailability.equals(that.itemsAvailability)) {
            return false;
        }
        if (!startDateTime.equals(that.startDateTime)) {
            return false;
        }
        return endDateTime.equals(that.endDateTime);
    }

    @Override
    public int hashCode() {
        int result = startDateTime.hashCode();
        result = HASHCODE_CONSTANT * result + endDateTime.hashCode();
        return result;
    }
}
