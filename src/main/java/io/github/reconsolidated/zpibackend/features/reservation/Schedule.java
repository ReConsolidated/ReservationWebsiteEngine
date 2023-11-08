package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(generator = "schedule_generator")
    private Long scheduleId;
    @OneToOne
    private Item item;
    //TODO ZPI-90 make it sorted
    //TODO API-90 addMethod
    // TODO ZPI-90 Validate that slots do not overlap
    @OneToMany
    private List<ScheduleSlot> scheduleSlots;//it must be sorted, and not overlapping

    public Schedule(Long scheduleId, Item item) {

        this.scheduleId = scheduleId;
        this.item = item;
        this.scheduleSlots = new ArrayList<>();

    }

    public void addSlot(ScheduleSlot scheduleSlot) {
        CoreConfig coreConfig = item.getStoreConfig().getCore();

        setSlotType(scheduleSlot);

        ScheduleSlot slotToAdd = scheduleSlot;

        for(int i = 0; i < scheduleSlots.size(); i++) {
            if(coreConfig.getFlexibility()) {
                if(scheduleSlots.get(i).startsEarlierThan(slotToAdd) &&
                        scheduleSlots.get(i).getEndDateTime().isAfter(slotToAdd.getStartDateTime())) {
                    //overlap by end of slot in schedule and front of new task
                    ScheduleSlot[] splitOld = scheduleSlots.get(i).split(slotToAdd.getStartDateTime());
                    ScheduleSlot[] splitNew = slotToAdd.split(scheduleSlots.get(i).getEndDateTime());
                    //remove then add in the same place, so it is safe
                    scheduleSlots.remove(i);
                    scheduleSlots.addAll(i, Arrays.asList(splitOld[0], splitOld[1].marge(splitNew[0])));
                    slotToAdd = splitNew[1];

                } else if(scheduleSlots.get(i).getStartDateTime().isBefore(slotToAdd.getEndDateTime()) &&
                        slotToAdd.startsEarlierThan(scheduleSlots.get(i))) {
                    //overlap by front of task in schedule and end of new task
                    ScheduleSlot[] splitOld = scheduleSlots.get(i).split(slotToAdd.getEndDateTime());
                    ScheduleSlot[] splitNew = slotToAdd.split(scheduleSlots.get(i).getStartDateTime());
                    //remove then add in the same place, so it is safe
                    scheduleSlots.remove(i);
                    scheduleSlots.addAll(i, Arrays.asList(splitNew[0], splitOld[0].marge(splitNew[1]), splitOld[1]));
                    return;

                } else if(slotToAdd.startsEarlierThan(scheduleSlots.get(i))) {
                    //new slot is before next slot but they are disjunctive
                    scheduleSlots.add(i, slotToAdd);
                    return;
                }
            } else {
                if(slotToAdd.startsEarlierThan(scheduleSlots.get(i))) {
                    //slot is before next slot so its its place
                    if(!slotToAdd.getEndDateTime().isAfter(scheduleSlots.get(i).getStartDateTime()) &&
                            (i == 0  || !scheduleSlots.get(i - 1).getEndDateTime().isAfter(slotToAdd.getStartDateTime()))) {
                        //slots do not overlap
                        scheduleSlots.add(i, slotToAdd);
                        return;
                    } else {
                        //slots overlap
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
        //it is first slot in the list or last slot in existing schedule
        scheduleSlots.add(slotToAdd);
    }
    //TODO optimize
    public void addSlots(List<ScheduleSlot> scheduleSlots) {
        for(ScheduleSlot slot : scheduleSlots){
            addSlot(slot);
        }
    }
    private void setSlotType(ScheduleSlot scheduleSlot) {

        CoreConfig core = item.getStoreConfig().getCore();

        if(core.getGranularity()) {
            scheduleSlot.setType(ReservationType.SLOT);
        }
        else if(core.getIsAllowOvernight()) {
            //slots available on same day as new scheduleSlot
            List<ScheduleSlot> daySlots = scheduleSlots.stream()
                    .filter(slot ->
                            slot.getStartDateTime().getYear() == scheduleSlot.getStartDateTime().getYear() && slot.getStartDateTime().getDayOfYear() == scheduleSlot.getStartDateTime().getDayOfYear())
                    .toList();
            boolean isLastOnDay = true;
            if (!daySlots.isEmpty()) {
                //new slot starts earlier than first slot of a day -> adjust morning event
                if (scheduleSlot.startsEarlierThan(daySlots.get(0))) {
                    ScheduleSlot daySlot = daySlots.get(0);
                    int index = scheduleSlots.indexOf(daySlot);

                    if (daySlot.getType() == ReservationType.MORNING) {
                        scheduleSlots.remove(index);
                    }
                    ScheduleSlot morningSlot = ScheduleSlot.builder()
                            .startDateTime(scheduleSlot.getStartDateTime())
                            .endDateTime(scheduleSlot.getStartDateTime())
                            .amount(scheduleSlot.getAmount())
                            .capacity(scheduleSlot.getCapacity())
                            .type(ReservationType.MORNING)
                            .build();
                    scheduleSlots.add(index, morningSlot);
                    scheduleSlot.setType(ReservationType.CONTINUOUS);
                } else {
                    //check whether scheduleSlot is last on its day
                    for (ScheduleSlot slot : daySlots) {
                        if (slot.endsLaterThan(scheduleSlot)) {
                            isLastOnDay = false;
                            break;
                        }
                    }
                    if (isLastOnDay) {
                        //last on a day
                        scheduleSlot.setType(ReservationType.OVERNIGHT);
                    } else {
                        //slot between other slots
                        scheduleSlot.setType(ReservationType.CONTINUOUS);
                    }
                }
            } else {
                //first and last slot of a day
                scheduleSlot.setType(ReservationType.OVERNIGHT);
            }

            if (scheduleSlot.getType() == ReservationType.OVERNIGHT) {
                ScheduleSlot previouslyLastDaySlot = daySlots.get(daySlots.size() - 1);

                previouslyLastDaySlot.setType(ReservationType.CONTINUOUS);
                int previouslyLastIndex = scheduleSlots.indexOf(previouslyLastDaySlot);
                //schedule is sorted so next slot must be next day morning if it does not exist use start of day as
                //in day of scheduleSlot
                ScheduleSlot nextMorningSlot = previouslyLastIndex + 1 < scheduleSlots.size() ?
                        scheduleSlots.get(previouslyLastIndex + 1)
                        : daySlots.get(0);

                if (nextMorningSlot.getType() != ReservationType.MORNING ||
                        previouslyLastIndex + 1 < scheduleSlots.size()) {

                    ScheduleSlot morningSlot = ScheduleSlot.builder()
                            .startDateTime(nextMorningSlot.getStartDateTime())
                            .endDateTime(nextMorningSlot.getStartDateTime())
                            .amount(scheduleSlot.getAmount())
                            .capacity(scheduleSlot.getCapacity())
                            .type(ReservationType.MORNING)
                            .build();
                    scheduleSlots.add(previouslyLastIndex + 1, morningSlot);
                }
            }
        } else if (core.getFlexibility()) {
            //TODO verify it
            scheduleSlot.setType(ReservationType.CONTINUOUS);
        } else {
            scheduleSlot.setType(ReservationType.NONE);
        }
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

        boolean verified = false;
        LocalDateTime prevSlotEndTime = scheduleSlots.get(0).getStartDateTime();

        if(!prevSlotEndTime.isAfter(startDate)) {

            for (ScheduleSlot currSlot : scheduleSlots) {
                if (currSlot.getStartDateTime().isAfter(prevSlotEndTime) ||
                        currSlot.getAmount() < amount ||
                        currSlot.getCapacity() < places) {
                    break;
                }
                if (currSlot.getEndDateTime().isAfter(endDate)) {
                    verified = true;
                    break;
                }
            }
        }
        return verified;
    }

    private boolean verifyNotFlexible(LocalDateTime startDate, LocalDateTime endDate, Integer amount, Integer places) {

        if(scheduleSlots.isEmpty()) {
            return false;
        }
        ScheduleSlot tmpSlot = new ScheduleSlot(null, startDate, endDate, amount, places, ReservationType.NONE);

        return scheduleSlots.stream().anyMatch(scheduleSlot -> scheduleSlot.equalsTimeFit(tmpSlot));
    }

    /**
     * Before calling make sure that scheduleSlot was verified with schedule using
     * verifyFlexible() or verifyNotFlexible() methods
     */
    public void processReservation(Reservation reservation) {

        ScheduleSlot scheduleSlot = reservation.getScheduleSlot();

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
            slot.setAmount(slot.getAmount() - reservation.getAmount());
            slot.setCapacity(slot.getCapacity() - reservation.getPlaces());
            if(slot.getAmount() == 0) {
                scheduleSlots.remove(slot);
            }
            if(slot.getCapacity() == 0) {
                scheduleSlots.remove(slot);
            }
        }
    }
}
