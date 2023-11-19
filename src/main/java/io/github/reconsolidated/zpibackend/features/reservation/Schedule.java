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
    @OneToMany
    private List<ScheduleSlot> scheduleSlots; //it must be sorted, and not overlapping

    public Schedule(Long scheduleId, Item item) {

        this.scheduleId = scheduleId;
        this.item = item;
        this.scheduleSlots = new ArrayList<>();

    }

    public void addSlot(ScheduleSlot scheduleSlot) {

        setSlotType(scheduleSlot);

        for (int i = 0; i < scheduleSlots.size(); i++) {

            if (scheduleSlot.startsEarlierThan(scheduleSlots.get(i))) {
                //slot is before next slot, so it is this slot place
                if (!scheduleSlot.getEndDateTime().isAfter(scheduleSlots.get(i).getStartDateTime()) &&
                        (i == 0  || !scheduleSlots.get(i - 1).getEndDateTime().isAfter(scheduleSlot.getStartDateTime()))) {
                    //slots do not overlap
                    scheduleSlots.add(i, scheduleSlot);
                    return;
                } else {
                    //slots overlap
                    throw new IllegalArgumentException();
                }
            }
        }
        //it is first slot in the list or last slot in existing schedule
        scheduleSlots.add(scheduleSlot);
    }

    public void addSlots(List<ScheduleSlot> scheduleSlots) {
        for (ScheduleSlot slot : scheduleSlots) {
            addSlot(slot);
        }
    }
    private void setSlotType(ScheduleSlot scheduleSlot) {

        CoreConfig core = item.getStore().getStoreConfig().getCore();

        if (core.getGranularity()) {
            scheduleSlot.setType(ReservationType.SLOT);
        } else if (core.getIsAllowOvernight()) {
            //slots available on same day as new scheduleSlot
            List<ScheduleSlot> daySlots = scheduleSlots.stream()
                    .filter(slot ->
                            slot.getStartDateTime().getYear() == scheduleSlot.getStartDateTime().getYear() &&
                            slot.getStartDateTime().getDayOfYear() == scheduleSlot.getStartDateTime().getDayOfYear())
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
                    ScheduleSlot morningSlot = new ScheduleSlot(scheduleSlot.getStartDateTime(),
                            scheduleSlot.getStartDateTime(), scheduleSlot.getAmount(), ReservationType.MORNING);
                    scheduleSlots.add(index, morningSlot);
                    scheduleSlot.setType(ReservationType.CONTINUOUS);
                } else if (daySlots.size() == 1 && daySlots.get(0).getType() == ReservationType.MORNING) {
                    scheduleSlot.setType(ReservationType.OVERNIGHT);
                    int index = scheduleSlots.indexOf(daySlots.get(0));
                    scheduleSlots.remove(index);
                    scheduleSlots.addAll(index,
                            Arrays.asList(new ScheduleSlot(scheduleSlot.getStartDateTime(),
                                            scheduleSlot.getStartDateTime(), scheduleSlot.getAmount(), ReservationType.MORNING),
                                    new ScheduleSlot(scheduleSlot.getStartDateTime().plusDays(1),
                                            scheduleSlot.getStartDateTime().plusDays(1), scheduleSlot.getAmount(), ReservationType.MORNING)));
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
                        ScheduleSlot previouslyLastDaySlot = daySlots.get(daySlots.size() - 1);
                        previouslyLastDaySlot.setType(ReservationType.CONTINUOUS);

                    } else {
                        //slot between other slots
                        scheduleSlot.setType(ReservationType.CONTINUOUS);
                    }
                }
            } else {
                //first and last slot of a day
                scheduleSlot.setType(ReservationType.OVERNIGHT);
                List<ScheduleSlot> nextDaySlots = scheduleSlots.stream()
                        .filter(slot ->
                                slot.getStartDateTime().getYear() == scheduleSlot.getStartDateTime().getYear() && slot.getStartDateTime().getDayOfYear() == scheduleSlot.getStartDateTime().getDayOfYear() + 1)
                        .toList();
                if (nextDaySlots.isEmpty()) {
                    scheduleSlots.add(new ScheduleSlot(scheduleSlot.getStartDateTime().plusDays(1),
                            scheduleSlot.getStartDateTime().plusDays(1), scheduleSlot.getAmount(), ReservationType.MORNING));
                } else {
                    scheduleSlots.add(new ScheduleSlot(nextDaySlots.get(0).getStartDateTime(),
                                    nextDaySlots.get(0).getStartDateTime(), scheduleSlot.getAmount(), ReservationType.MORNING));
                }

            }
        } else {
            scheduleSlot.setType(ReservationType.CONTINUOUS);
        }
    }

    public boolean verify(boolean granularity, ScheduleSlot scheduleSlot) {

        if (granularity) {
            return verifyGranular(scheduleSlot);
        } else {
            return verifyNotGranular(scheduleSlot);
        }
    }

    private boolean verifyNotGranular(ScheduleSlot scheduleSlot) {

        if (scheduleSlots.isEmpty()) {
            return false;
        }
        scheduleSlots.sort(Comparator.comparing(ScheduleSlot::getStartDateTime));

        boolean verified = false;

        List<ScheduleSlot> toVerify = new ArrayList<>(scheduleSlots.stream()
                .filter(slot -> slot.overlap(scheduleSlot))
                .toList());
        LocalDateTime prevSlotEndTime = toVerify.get(0).getStartDateTime();

        if (!prevSlotEndTime.isAfter(scheduleSlot.getStartDateTime())) {

            for (ScheduleSlot currSlot : toVerify) {
                if (currSlot.getStartDateTime().isAfter(prevSlotEndTime) ||
                        currSlot.getAmount() < scheduleSlot.getAmount()) {
                    break;
                }
                if (currSlot.getEndDateTime().isAfter(scheduleSlot.getEndDateTime())) {
                    verified = true;
                    break;
                }
            }
        }
        return verified && findAvailableItem(toVerify).size() >= scheduleSlot.getAmount();
    }

    private boolean verifyGranular(ScheduleSlot scheduleSlot) {

        if (scheduleSlots.isEmpty()) {
            return false;
        }

        List<ScheduleSlot> toVerify = scheduleSlots.stream()
                .filter(slot -> slot.equalsTime(scheduleSlot))
                .toList();

        return toVerify.size() == 1 && toVerify.get(0).getAmount() >= scheduleSlot.getAmount();
    }


    /**
     Use verify method before calling processReservation()*/
    public void processReservation(Reservation reservation) {

        List<ScheduleSlot> toReserve = new ArrayList<>(scheduleSlots.stream()
                .filter(slot -> slot.overlap(reservation.getScheduleSlot()))
                .toList());

        List<Integer> availableItems = findAvailableItem(toReserve);
        if (availableItems.size() < reservation.getAmount()) {
            throw new IllegalArgumentException();
        }
        if (toReserve.get(0).getStartDateTime().isBefore(reservation.getStartDateTime())) {
            ScheduleSlot first = toReserve.get(0);
            toReserve.remove(0);
            int scheduleIndex = scheduleSlots.indexOf(first);
            scheduleSlots.remove(scheduleIndex);
            ScheduleSlot[] split = first.split(reservation.getStartDateTime());
            setSlotType(split[0]);
            setSlotType(split[1]);
            scheduleSlots.addAll(scheduleIndex, Arrays.asList(split));
            toReserve.add(0, split[1]);
        }
        if (toReserve.get(toReserve.size() - 1).getEndDateTime().isAfter(reservation.getEndDateTime())) {
            ScheduleSlot last = toReserve.get(toReserve.size() - 1);
            toReserve.remove(toReserve.size() - 1);
            int scheduleIndex = scheduleSlots.indexOf(last);
            scheduleSlots.remove(scheduleIndex);
            ScheduleSlot[] split = last.split(reservation.getEndDateTime());
            setSlotType(split[0]);
            setSlotType(split[1]);
            scheduleSlots.addAll(scheduleIndex, Arrays.asList(split));
            toReserve.add(split[0]);
        }

        for (ScheduleSlot slot: toReserve) {
            slot.setAmount(slot.getAmount() - reservation.getAmount());
            for (int i = 0; i < reservation.getAmount(); i++) {
                slot.getItemsAvailability().set(availableItems.get(i), false);
            }
            if (slot.getAmount() == 0) {
                scheduleSlots.remove(slot);
            }
        }
        ArrayList<Long> subItemId = new ArrayList<>();
        for (int i = 0; i < reservation.getAmount(); i++) {
            subItemId.add(Long.valueOf(availableItems.get(i)));
        }
        reservation.setSubItemIdList(subItemId);
    }

    public List<Integer> findAvailableItem(List<ScheduleSlot> slotsToVerify) {

        LinkedList<Integer> availableItems = new LinkedList<>();
        for (int i = 0; i < item.getAmount(); i++) {
            availableItems.add(i);
        }
        for (ScheduleSlot slot : slotsToVerify) {
            availableItems.removeIf(index -> !slot.getItemsAvailability().get(index));
        }
        return availableItems;
    }

    public void processReservationRemoval(Reservation reservation) {

    }
}
