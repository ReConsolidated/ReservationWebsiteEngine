package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(generator = "schedule_generator")
    private Long scheduleId;
    //TODO ZPI-90 make it sorted
    //TODO API-90 addMethod
    // TODO ZPI-90 Validate that slots do not overlap
    @OneToMany
    private List<ScheduleSlot> scheduleSlots;

    public void addSlot(ScheduleSlot scheduleSlot){

    }

    public void addSlots(List<ScheduleSlot> scheduleSlot){

    }

    public void removeSlot(ScheduleSlot scheduleSlot){

    }

    public boolean verify(boolean flexibility, LocalDateTime startDate, LocalDateTime endDate, Integer amount,
                          Integer places) {

        if(flexibility) {
            return verifyFlexible(startDate, endDate, amount, places);
        }
        else {
            return verifyNotFlexible(startDate, endDate, amount, places);
        }
    }

    private boolean verifyFlexible(LocalDateTime startDate, LocalDateTime endDate, Integer amount, Integer places) {

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

    private boolean verifyNotFlexible(LocalDateTime startDate, LocalDateTime endDate, Integer amount, Integer places) {

        if(scheduleSlots.isEmpty()) {
            return false;
        }
        ScheduleSlot tmpSlot = new ScheduleSlot(null, startDate, endDate, amount, places, ReservationType.NONE);

        return scheduleSlots.stream().anyMatch(scheduleSlot -> scheduleSlot.equalsTimeFit(tmpSlot));
    }

    /**
     Before calling make sure that scheduleSlot was verified with schedule using
     verifyFlexible() or verifyNotFlexible() methods
    */
    public Boolean processReservation(ScheduleSlot scheduleSlot, int amount, int places) {

        List<ScheduleSlot> toReserve = new java.util.ArrayList<>(scheduleSlots.stream()
                .filter(slot -> slot.overlap(scheduleSlot))
                .toList());

        if(toReserve.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if(toReserve.get(0).getStartDateTime().isBefore(scheduleSlot.getStartDateTime())) {
            ScheduleSlot first = toReserve.get(0);
            toReserve.remove(0);
            scheduleSlots.remove(first);
            ScheduleSlot[] split = first.split(scheduleSlot.getStartDateTime());
            addSlots(Arrays.asList(split));
            toReserve.add(0, split[1]);
        }
        if(toReserve.get(toReserve.size() - 1).getEndDateTime().isAfter(scheduleSlot.getEndDateTime())) {
            ScheduleSlot last = toReserve.get(toReserve.size() - 1);
            toReserve.remove(toReserve.size() - 1);
            scheduleSlots.remove(last);
            ScheduleSlot[] split = last.split(scheduleSlot.getEndDateTime());
            addSlots(Arrays.asList(split));
            toReserve.add(split[0]);
        }

        for(ScheduleSlot slot: toReserve) {
            slot.setAmount(slot.getAmount() - amount);
            slot.setCapacity(slot.getCapacity() - places);
            if(slot.getAmount() == 0) {
                scheduleSlots.remove(slot);
            }
            if(slot.getCapacity() == 0) {
                scheduleSlots.remove(slot);
            }
        }

        return false;
    }
}
