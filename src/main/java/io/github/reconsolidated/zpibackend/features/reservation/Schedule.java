package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    //TODO ZPI-90 Validate that slots do not overlap
    @Id
    @GeneratedValue(generator = "schedule_generator")
    private Long scheduleId;
    @OneToMany
    private List<ScheduleSlot> scheduleSlots;

    public boolean verifyFlexible(LocalDateTime startDate, LocalDateTime endDate, Integer amount, Integer places){
        if(scheduleSlots.isEmpty()) {
            return false;
        }
        //TODO ZPI-90 make it always sorted
        scheduleSlots.sort(Comparator.comparing(ScheduleSlot::getStartDateTime));

        boolean varified = false;
        LocalDateTime prevSlotEndTime = scheduleSlots.get(0).getStartDateTime();

        if(!prevSlotEndTime.isAfter(startDate)) {
            Iterator<ScheduleSlot> iterator = scheduleSlots.listIterator();

            while (iterator.hasNext()) {
                ScheduleSlot currSlot = iterator.next();
                if(currSlot.getStartDateTime().isAfter(prevSlotEndTime) ||
                        currSlot.getAmount() < amount ||
                        currSlot.getCapacity() < places) {
                    break;
                }
                if(currSlot.getEndDateTime().isAfter(endDate)) {
                    varified = true;
                    break;
                }
            }
        }
        return varified;
    }

    public boolean verifyNotFlexible(LocalDateTime startDate, LocalDateTime endDate, Integer amount, Integer places){
        if(scheduleSlots.isEmpty()) {
            return false;
        }
        ScheduleSlot tmpSlot = new ScheduleSlot(null, startDate, endDate, amount, places, ReservationType.NONE);

        return scheduleSlots.stream().anyMatch(scheduleSlot -> scheduleSlot.equalsTimeFit(tmpSlot));
    }

    public Boolean processReservation(ScheduleSlot scheduleSlot){

        return false;
    }


}
