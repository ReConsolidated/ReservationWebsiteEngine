package io.github.reconsolidated.zpibackend.domain.reservation;

import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation.BasicReservationStrategy;
import io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation.FlexibleReservationStrategy;
import io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation.NotUniqueReservationStrategy;
import io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation.SimultaneousReservationStrategy;
import io.github.reconsolidated.zpibackend.domain.reservation.strategy.time.ContinuousTimeStrategy;
import io.github.reconsolidated.zpibackend.domain.reservation.strategy.time.FlexibleTimeStrategy;
import io.github.reconsolidated.zpibackend.domain.reservation.strategy.time.GranularTimeStrategy;
import io.github.reconsolidated.zpibackend.domain.storeConfig.CoreConfig;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private static final int OVERNIGHT_DURATION = 30;
    @Id
    @GeneratedValue(generator = "schedule_generator")
    private Long scheduleId;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Item item;
    @OrderBy("startDateTime ASC, endDateTime ASC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "schedule", orphanRemoval = true)
    private List<ScheduleSlot> availableScheduleSlots = new ArrayList<>(); //it must be sorted, and not overlapping
    private static final int WEEKDAYS = 7;

    public Schedule(Long scheduleId, Item item) {
        this.scheduleId = scheduleId;
        this.item = item;
        this.availableScheduleSlots = new ArrayList<>();
    }

    public Schedule(Item item, List<Availability> availabilities) {

        this.item = item;
        this.availableScheduleSlots = new ArrayList<>();
        for (Availability availability: availabilities) {
            addSlot(new ScheduleSlot(availability.getStartDateTime(), availability.getEndDateTime(), item.getAmount()));
        }

    }

    public List<Availability> getAvailabilities() {
        return availableScheduleSlots.stream().map((slot) ->
                new Availability(
                        slot.getStartDateTime(),
                        slot.getEndDateTime(),
                        slot.getType()
                )).collect(Collectors.toList());
    }

    public List<Availability> getLongestAvailabilities() {
        if (availableScheduleSlots.isEmpty()) {
            return new ArrayList<>();
        }
        List<Availability> availabilities = new ArrayList<>();
        Availability newAvailability = new Availability();
        newAvailability.setType(availableScheduleSlots.get(0).getType());
        newAvailability.setStartDateTime(availableScheduleSlots.get(0).getStartDateTime());
        for (int i = 0; i < availableScheduleSlots.size() - 1; i++) {
            if (!availableScheduleSlots.get(i).getEndDateTime().isEqual(availableScheduleSlots.get(i + 1).getStartDateTime()) ||
                    availableScheduleSlots.get(i + 1).getType() == ReservationType.OVERNIGHT ||
                    availableScheduleSlots.get(i + 1).getType() == ReservationType.MORNING ||
                    availableScheduleSlots.get(i).getType() == ReservationType.OVERNIGHT ||
                    availableScheduleSlots.get(i).getType() == ReservationType.MORNING) {
                newAvailability.setEndDateTime(availableScheduleSlots.get(i).getEndDateTime());
                availabilities.add(newAvailability);
                newAvailability = new Availability();
                newAvailability.setType(availableScheduleSlots.get(i + 1).getType());
                newAvailability.setStartDateTime(availableScheduleSlots.get(i + 1).getStartDateTime());
            }
        }
        newAvailability.setEndDateTime(availableScheduleSlots.get(availableScheduleSlots.size() - 1).getEndDateTime());
        availabilities.add(newAvailability);
        return availabilities;
    }

    public List<Availability> getAvailabilitiesForSubItems(List<Integer> subItemsId) {
        return availableScheduleSlots.stream()
                .filter(
                        slot -> subItemsId.stream()
                                .allMatch(id -> slot.getItemsAvailability().get(id)))
                .map((slot) -> new Availability(
                        slot.getStartDateTime(),
                        slot.getEndDateTime(),
                        slot.getType()
                )).collect(Collectors.toList());
    }

    public void addSlot(ScheduleSlot scheduleSlot) {
        scheduleSlot.setSchedule(this);
        setSlotType(scheduleSlot);

        for (int i = 0; i < availableScheduleSlots.size(); i++) {

            if (scheduleSlot.startsEarlierThan(availableScheduleSlots.get(i))) {
                //slot is before next slot, so it is this slot place
                if ((!scheduleSlot.getEndDateTime().isAfter(availableScheduleSlots.get(i).getStartDateTime())) &&
                        (i == 0  || !availableScheduleSlots.get(i - 1).getEndDateTime().isAfter(scheduleSlot.getStartDateTime()))) {
                    //slots do not overlap
                    availableScheduleSlots.add(i, scheduleSlot);
                    return;
                } else {
                    //slots overlap
                    throw new IllegalArgumentException("Can not add slot that overlap with existing slot!");
                }
            }
        }
        //it is first slot in the list or last slot in existing schedule
        availableScheduleSlots.add(scheduleSlot);
    }

    private void setSlotType(ScheduleSlot scheduleSlot) {

        CoreConfig core = item.getStore().getStoreConfig().getCore();

        if (core.getGranularity()) {
            scheduleSlot.setType(ReservationType.SLOT);
        } else if (core.getAllowOverNight()) {
            scheduleSlot.setType(ReservationType.CONTINUOUS);
            //slots available on same day as new scheduleSlot
            List<ScheduleSlot> daySlots = availableScheduleSlots.stream()
                    .filter(slot ->
                            slot.getStartDateTime().plusHours(1).getYear() == scheduleSlot.getStartDateTime().plusHours(1).getYear() &&
                            slot.getStartDateTime().plusHours(1).getDayOfYear() == scheduleSlot.getStartDateTime().plusHours(1).getDayOfYear())
                    .toList();
            boolean isLastOnDay = true;
            if (!daySlots.isEmpty()) {
                //new slot starts earlier than first slot of a day -> adjust morning event
                if (scheduleSlot.startsEarlierThan(daySlots.get(0))) {
                    ScheduleSlot daySlot = daySlots.get(0);
                    int index = availableScheduleSlots.indexOf(daySlot);

                    if (daySlot.getType() == ReservationType.MORNING) {
                        availableScheduleSlots.remove(index);
                    }
                    ScheduleSlot morningSlot = new ScheduleSlot(
                            scheduleSlot.getStartDateTime().minusMinutes(OVERNIGHT_DURATION),
                            scheduleSlot.getStartDateTime(),
                            scheduleSlot.getItemsAvailability().size(),
                            ReservationType.MORNING);
                    availableScheduleSlots.add(index, morningSlot);
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
                        ScheduleSlot previousOvernightSlot = daySlots.get(daySlots.size() - 1);
                        int overnightIndex = availableScheduleSlots.indexOf(previousOvernightSlot);
                        if (previousOvernightSlot.getType() == ReservationType.OVERNIGHT) {
                            availableScheduleSlots.remove(overnightIndex);
                        }
                        availableScheduleSlots.add(
                                overnightIndex,
                                new ScheduleSlot(
                                        scheduleSlot.getEndDateTime(),
                                        scheduleSlot.getEndDateTime().plusMinutes(OVERNIGHT_DURATION),
                                        scheduleSlot.getItemsAvailability().size(),
                                        ReservationType.OVERNIGHT));
                    }
                }
            } else {
                int index = 0;
                for (int i = 0; i < availableScheduleSlots.size(); i++) {
                    if (!availableScheduleSlots.get(i).getEndDateTime().isAfter(scheduleSlot.getStartDateTime()) &&
                            (i + 1 >= availableScheduleSlots.size() ||
                                    !availableScheduleSlots.get(i + 1).getStartDateTime().isBefore(scheduleSlot.getEndDateTime()))) {
                        index = i + 1;
                        break;
                    }
                }
                //first and last slot of a day
                availableScheduleSlots.addAll(
                        index,
                        Arrays.asList(
                            new ScheduleSlot(
                                    scheduleSlot.getStartDateTime().minusMinutes(OVERNIGHT_DURATION),
                                    scheduleSlot.getStartDateTime(),
                                    scheduleSlot.getItemsAvailability().size(),
                                    ReservationType.MORNING),
                            new ScheduleSlot(
                                    scheduleSlot.getEndDateTime(),
                                    scheduleSlot.getEndDateTime().plusMinutes(OVERNIGHT_DURATION),
                                    scheduleSlot.getItemsAvailability().size(),
                                    ReservationType.OVERNIGHT)));
            }
        } else {
            scheduleSlot.setType(ReservationType.CONTINUOUS);
        }
    }

    public void removeSlot(ScheduleSlot slot) {

        if (slot.getType() == ReservationType.CONTINUOUS) {
            int index = availableScheduleSlots.indexOf(slot);
            if (index != -1) {
                if (index + 1 < availableScheduleSlots.size()) {
                    if (availableScheduleSlots.get(index + 1).getType() == ReservationType.OVERNIGHT) {
                        if (index + 2 < availableScheduleSlots.size()) {
                            if (availableScheduleSlots.get(index + 2).getType() == ReservationType.MORNING) {
                                availableScheduleSlots.remove(index + 2);
                            }
                        }
                        availableScheduleSlots.remove(index + 1);
                    }
                }
                availableScheduleSlots.remove(index);
            }
        } else {
            availableScheduleSlots.remove(slot);
        }
    }

    private FlexibleTimeStrategy getTimeStrategy(CoreConfig core) {
        if (core.getGranularity()) {
            return GranularTimeStrategy.getInstance();
        } else {
            return ContinuousTimeStrategy.getInstance();
        }
    }

    private FlexibleReservationStrategy getReservationStrategy(CoreConfig core) {
        if (!core.getUniqueness()) {
            return NotUniqueReservationStrategy.getInstance();
        }
        if (core.getSimultaneous()) {
            return SimultaneousReservationStrategy.getInstance();
        }
        return BasicReservationStrategy.getInstance();
    }
    public boolean verify(CoreConfig coreConfig, ScheduleSlot scheduleSlot) {
        if (!scheduleSlot.getStartDateTime().isBefore(scheduleSlot.getEndDateTime())) {
            return false;
        }
        //list of slots that are in boundaries of given schedule slot
        List<ScheduleSlot> toVerify = availableScheduleSlots.stream()
                .filter(slot -> slot.overlap(scheduleSlot))
                .toList();
        //verification of continuity of slots and boundaries
        if (!getTimeStrategy(coreConfig).verifyScheduleSlots(this, toVerify, scheduleSlot)) {
            return false;
        }
        return getReservationStrategy(coreConfig).verifyReservationFitting(toVerify, scheduleSlot);
    }

    public boolean processReservation(CoreConfig core, Reservation reservation) {
        List<ScheduleSlot> toReserve = availableScheduleSlots.stream()
                .filter(slot -> slot.overlap(reservation.getScheduleSlot()))
                .toList();

        if (!getTimeStrategy(core).verifyScheduleSlots(this, toReserve, reservation.getScheduleSlot())) {
            return false;
        }
        toReserve = getTimeStrategy(core).prepareSchedule(this, reservation, toReserve);

        return getReservationStrategy(core).processReservation(this, reservation, toReserve);
    }

    /**
     Returns list of slots that are suggested based on given slot.
     Suggestions can be categorised into:
     - the longest possible slots on the same day and with same amount as original slot
     - if free slot in same time but day before
     - if free slot in same time but day after
     - if free slot in same time but week after
     */
    public List<ScheduleSlot> suggest(CoreConfig coreConfig, ScheduleSlot originalSlot) {

        List<ScheduleSlot> fittingDaySlots = availableScheduleSlots.stream()
                .filter(slot ->
                        slot.getStartDateTime().getYear() == originalSlot.getStartDateTime().getYear() &&
                        slot.getStartDateTime().getDayOfYear() == originalSlot.getStartDateTime().getDayOfYear())
                .toList();
        //same day suggestions
        ArrayList<ScheduleSlot> suggestions = new ArrayList<>(
                getLongestSlotsWithAmount(originalSlot.getCurrAmount(), fittingDaySlots));

        ScheduleSlot dayBeforeSlot = new ScheduleSlot(
                originalSlot.getStartDateTime().minusDays(1),
                originalSlot.getEndDateTime().minusDays(1),
                item.getAmount());
        if (verify(coreConfig, dayBeforeSlot)) {
            suggestions.add(dayBeforeSlot);
        }
        ScheduleSlot dayAfterSlot = new ScheduleSlot(
                originalSlot.getStartDateTime().plusDays(1),
                originalSlot.getEndDateTime().plusDays(1),
                item.getAmount());
        if (verify(coreConfig, dayAfterSlot)) {
            suggestions.add(dayAfterSlot);
        }
        ScheduleSlot weekAfterSlot = new ScheduleSlot(
                originalSlot.getStartDateTime().plusDays(WEEKDAYS),
                originalSlot.getEndDateTime().plusDays(WEEKDAYS),
                item.getAmount());
        if (verify(coreConfig, weekAfterSlot)) {
            suggestions.add(weekAfterSlot);
        }

        return suggestions;
    }

    /**
     Returns list of the longest possible slots that can be reserved with given amount
     */
    private List<ScheduleSlot> getLongestSlotsWithAmount(int amount, List<ScheduleSlot> scheduleSlots) {
        //result list of longest found slots that have sufficient amount
        ArrayList<ScheduleSlot> longestSlots = new ArrayList<>();
        //getting rid of slots with to low amount to optimize search
        List<ScheduleSlot> slotsToCheck = scheduleSlots.stream().filter(slot -> slot.getCurrAmount() >= amount).toList();
        //slot can't have a gap inside, so we only have to search through continuous groups of slots
        List<ArrayList<ScheduleSlot>> continuousSlotsLists = getContinuousSlotsLists(slotsToCheck);

        //for each continuous group of slots
        for (ArrayList<ScheduleSlot> continuousSlots : continuousSlotsLists) {
            longestSlots.addAll(getLongestSlotsWithAmountFromContinuousSlots(amount, continuousSlots));
        }
        return longestSlots;
    }

    private List<ScheduleSlot> getLongestSlotsWithAmountFromContinuousSlots(int amount, List<ScheduleSlot> continuousSlots) {

        ArrayList<ScheduleSlot> longestSlots = new ArrayList<>();

        //each slot can be a start of the longest possible slot
        for (ScheduleSlot slot : continuousSlots) {
            ScheduleSlot longestSlot = ScheduleSlot.builder()
                    .startDateTime(slot.getStartDateTime())
                    .currAmount(slot.getCurrAmount())
                    .build();
            //index of current first slot in continuous group of slots
            int startIndex = continuousSlots.indexOf(slot);
            //list of items that are continuously available
            ArrayList<Integer> subItemIndexes = slot.getAvailableItemsIndexes();
            //flag for optimization
            // if is true we don't have to check later slots because they are used in the longest possible slot
            boolean coverAllSlots = true;
            for (int i = startIndex + 1; i < continuousSlots.size(); i++) {

                ScheduleSlot slotToCheck = continuousSlots.get(i);
                ArrayList<Integer> subItemIndexesTmp = new ArrayList<>(subItemIndexes);
                //checking availability of each sub item
                for (int j = 0; j < item.getAmount(); j++) {
                    if (subItemIndexesTmp.contains(j) && !slotToCheck.getItemsAvailability().get(j)) {
                        subItemIndexesTmp.remove(Integer.valueOf(j));
                    }
                }
                //ending slot
                if (subItemIndexesTmp.size() < amount) {
                    longestSlot.setEndDateTime(continuousSlots.get(i - 1).getEndDateTime());
                    longestSlot.setItemsAvailability(item.getAmount(), subItemIndexes);
                    longestSlot.setCurrAmount(subItemIndexes.size());
                    longestSlots.add(longestSlot);
                    coverAllSlots = false;
                    break;
                } else {
                    subItemIndexes = subItemIndexesTmp;
                }
            }
            //optimization if we have the longest possible slot containing all left slots in group
            if (coverAllSlots) {
                longestSlot.setEndDateTime(continuousSlots.get(continuousSlots.size() - 1).getEndDateTime());
                longestSlot.setItemsAvailability(item.getAmount(), subItemIndexes);
                longestSlot.setCurrAmount(subItemIndexes.size());
                longestSlots.add(longestSlot);
                break;
            }
        }
        return longestSlots;
    }

    /**
     Groups schedule slots from passed schedule slots list in groups that covers continuous time periods
     */
    private List<ArrayList<ScheduleSlot>> getContinuousSlotsLists(List<ScheduleSlot> scheduleSlots) {
        if (scheduleSlots.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ArrayList<ScheduleSlot>> continuousSlots = new ArrayList<>();
        ArrayList<ScheduleSlot> currList = new ArrayList<>();
        ScheduleSlot prev = scheduleSlots.get(0);

        for (ScheduleSlot slot: scheduleSlots) {
            if (prev.isContinuousWith(slot) || prev.equalsTime(slot)) {
                currList.add(slot);
            } else {
                continuousSlots.add(currList);
                currList = new ArrayList<>();
                currList.add(slot);
            }
            prev = slot;
        }
        continuousSlots.add(currList);
        return continuousSlots;
    }

    public void processReservationRemoval(CoreConfig core, Reservation reservation) {

        ScheduleSlot reservationSlot = reservation.getScheduleSlot();
        List<ScheduleSlot> reservationSlots = availableScheduleSlots
                .stream()
                .filter(slot -> slot.overlap(reservationSlot))
                .collect(Collectors.toList());
        availableScheduleSlots.removeAll(reservationSlots);
        reservationSlots = getTimeStrategy(core).fillGaps(reservation, reservationSlots);

        List<ScheduleSlot> restoredSlots = getReservationStrategy(core).processReservationDelete(reservation, reservationSlots);
        restoredSlots.forEach(this::addSlot);
    }
}
