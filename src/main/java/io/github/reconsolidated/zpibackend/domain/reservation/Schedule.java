package io.github.reconsolidated.zpibackend.domain.reservation;

import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.item.Item;
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
    private static final int OVERNIGHT_DURATION = 1;
    @Id
    @GeneratedValue(generator = "schedule_generator")
    private Long scheduleId;
    @OneToOne
    private Item item;
    @OneToMany(cascade = CascadeType.ALL)
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
                            scheduleSlot.getCurrAmount(),
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
                                        scheduleSlot.getCurrAmount(),
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
                                    scheduleSlot.getCurrAmount(),
                                    ReservationType.MORNING),
                            new ScheduleSlot(
                                    scheduleSlot.getEndDateTime(),
                                    scheduleSlot.getEndDateTime().plusMinutes(OVERNIGHT_DURATION),
                                    scheduleSlot.getCurrAmount(),
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

    public boolean verify(CoreConfig coreConfig, ScheduleSlot scheduleSlot) {
        if (!scheduleSlot.getStartDateTime().isBefore(scheduleSlot.getEndDateTime())) {
            return false;
        }
        //list of slots that are in boundaries of given schedule slot
        List<ScheduleSlot> toVerify = availableScheduleSlots.stream()
                .filter(slot -> slot.overlap(scheduleSlot))
                .toList();
        //verification of continuity of slots and boundaries
        if (!verifyScheduleSlots(coreConfig.getGranularity(), toVerify, scheduleSlot)) {
            return false;
        }
        return verifyReservationFitting(coreConfig, toVerify, scheduleSlot);
    }

    private boolean verifyReservationFitting(CoreConfig coreConfig, List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
        if (!coreConfig.getUniqueness()) {
            return verifyReservationFittingNotUnique(scheduleSlots, reservationSlot);
        }
        if (coreConfig.getSimultaneous()) {
            return verifyReservationFittingSimultaneously(scheduleSlots, reservationSlot);
        }
        return true;
    }

    private boolean verifyReservationFittingSimultaneously(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {

        if (scheduleSlots.isEmpty()) {
            return false;
        }
        return scheduleSlots
                .stream()
                .allMatch(scheduleSlot -> scheduleSlot.getCurrAmount() >= reservationSlot.getCurrAmount());
    }

    private boolean verifyReservationFittingNotUnique(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {

        if (scheduleSlots.isEmpty()) {
            return false;
        }
        ArrayList<Integer> subItemIndexes = scheduleSlots.get(0).getAvailableItemsIndexes();
        for (ScheduleSlot currSlot : scheduleSlots) {
            //remove indexes that aren't available in all slots
            subItemIndexes.removeIf(index -> !currSlot.getAvailableItemsIndexes().contains(index));
            //amount is too small
            if (subItemIndexes.size() < reservationSlot.getCurrAmount()) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyScheduleSlots(boolean granularity, List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
        return granularity ?
                verifyScheduleSlotsGranular(scheduleSlots, reservationSlot) :
                verifyScheduleSlotsNotGranular(scheduleSlots, reservationSlot);
    }

    private boolean verifyScheduleSlotsGranular(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
        if (scheduleSlots.isEmpty()) {
            return false;
        }
        if (!scheduleSlots.get(0).getStartDateTime().equals(reservationSlot.getStartDateTime())) {
            return false;
        }
        if (!scheduleSlots.get(scheduleSlots.size() - 1).getEndDateTime().equals(reservationSlot.getEndDateTime())) {
            return false;
        }
        return checkSlotsContinuity(scheduleSlots);
    }

    private boolean verifyScheduleSlotsNotGranular(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
        if (scheduleSlots.isEmpty()) {
            return false;
        }
        if (scheduleSlots.get(0).getStartDateTime().isAfter(reservationSlot.getStartDateTime())) {
            return false;
        }
        if (scheduleSlots.get(scheduleSlots.size() - 1).getEndDateTime().isBefore(reservationSlot.getEndDateTime())) {
            return false;
        }
        return checkSlotsContinuity(scheduleSlots);
    }

    private boolean checkSlotsContinuity(List<ScheduleSlot> scheduleSlots) {
        for (int i = 0; i < scheduleSlots.size() - 1; i++) {
            if (!scheduleSlots.get(i).getEndDateTime().equals(scheduleSlots.get(i + 1).getStartDateTime()) &&
                    !(scheduleSlots.get(i).getType() == ReservationType.OVERNIGHT &&
                    scheduleSlots.get(i + 1).getType() == ReservationType.MORNING)) {
                return false;
            }
        }
        return true;
    }

    public boolean processReservation(CoreConfig core, Reservation reservation) {
        List<ScheduleSlot> toReserve = availableScheduleSlots.stream()
                .filter(slot -> slot.overlap(reservation.getScheduleSlot()))
                .toList();
        if (core.getGranularity()) {
            if (!verifyScheduleSlotsGranular(toReserve, reservation.getScheduleSlot())) {
                return false;
            }
        } else {
            if (!verifyScheduleSlotsNotGranular(toReserve, reservation.getScheduleSlot())) {
                return false;
            }
            toReserve = prepareScheduleNotGranular(reservation, toReserve);
        }
        if (!core.getUniqueness()) {
            return processReservationNotUnique(reservation, toReserve);
        } else {
            return processReservationUnique(reservation, toReserve);
        }
    }

    private List<ScheduleSlot> prepareScheduleNotGranular(Reservation reservation, List<ScheduleSlot> toReserve) {
        if (toReserve.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ScheduleSlot> preparedToReserve = new ArrayList<>(toReserve.size());
        preparedToReserve.addAll(toReserve);

        if (preparedToReserve.get(0).getStartDateTime().isBefore(reservation.getStartDateTime())) {
            ScheduleSlot first = preparedToReserve.get(0);
            int firstIndex = availableScheduleSlots.indexOf(first);
            availableScheduleSlots.remove(firstIndex);
            preparedToReserve.remove(0);
            ScheduleSlot[] split = first.split(reservation.getStartDateTime());
            setSlotType(split[0]);
            setSlotType(split[1]);
            availableScheduleSlots.addAll(firstIndex, Arrays.asList(split));
            preparedToReserve.add(0, split[1]);
        }
        if (preparedToReserve.get(preparedToReserve.size() - 1).getEndDateTime().isAfter(reservation.getEndDateTime())) {
            ScheduleSlot last = preparedToReserve.get(preparedToReserve.size() - 1);
            int lastIndex = availableScheduleSlots.indexOf(last);
            availableScheduleSlots.remove(lastIndex);
            preparedToReserve.remove(preparedToReserve.size() - 1);
            ScheduleSlot[] split = last.split(reservation.getEndDateTime());
            setSlotType(split[0]);
            setSlotType(split[1]);
            availableScheduleSlots.addAll(lastIndex, Arrays.asList(split));
            preparedToReserve.add(split[0]);
        }
        return preparedToReserve;
    }

    private boolean processReservationNotUnique(Reservation reservation, List<ScheduleSlot> toReserve) {
        if (toReserve.isEmpty()) {
            return false;
        }
        ArrayList<Integer> availableSubItemsId = toReserve.get(0).getAvailableItemsIndexes();
        for (ScheduleSlot slot : toReserve) {

            availableSubItemsId
                    .forEach(
                            index -> availableSubItemsId.removeIf(
                                    i -> !slot.getAvailableItemsIndexes().contains(i)));
            if (availableSubItemsId.size() < reservation.getAmount()) {
                return false;
            }
        }
        ArrayList<Long> reservedSubItemsIndexes = new ArrayList<>();
        for (int i = 0; i < reservation.getAmount(); i++) {
            reservedSubItemsIndexes.add(Long.valueOf(availableSubItemsId.get(i)));
        }
        for (ScheduleSlot slot : toReserve) {
            slot.setCurrAmount(slot.getCurrAmount() - reservation.getAmount());
            for (Long subItemId : reservedSubItemsIndexes) {
                slot.getItemsAvailability().set(subItemId.intValue(), false);
            }
            if (slot.getCurrAmount() == 0) {
                removeSlot(slot);
            }
        }
        reservation.setSubItemIdList(reservedSubItemsIndexes);
        return true;
    }

    private boolean processReservationUnique(Reservation reservation, List<ScheduleSlot> toReserve) {
        if (toReserve.isEmpty()) {
            return false;
        }
        for (ScheduleSlot slot : toReserve) {
            if (slot.getCurrAmount() >= reservation.getAmount()) {
                slot.setCurrAmount(slot.getCurrAmount() - reservation.getAmount());
                if (slot.getCurrAmount() == 0) {
                    removeSlot(slot);
                }
            } else {
                return false;
            }
        }
        return true;
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

    public void processReservationRemoval(Reservation reservation) {
        for (ScheduleSlot scheduleSlot : availableScheduleSlots) {
            if (scheduleSlot.equalsTime(reservation.getScheduleSlot())) {
                scheduleSlot.setCurrAmount(scheduleSlot.getCurrAmount() + reservation.getAmount());
                for (int i = 0; i < reservation.getAmount(); i++) {
                    scheduleSlot.getItemsAvailability().set(reservation.getSubItemIdList().get(i).intValue(), true);
                }
                return;
            }
        }
        for (int i = 0; i < reservation.getAmount(); i++) {
            int index = reservation.getSubItemIdList().get(i).intValue();
            List<Boolean> itemAvailability = reservation.getScheduleSlot().getItemsAvailability();
            while (index >= itemAvailability.size()) {
                itemAvailability.add(true);
            }
            itemAvailability.set(index, true);
            reservation.getScheduleSlot().setItemsAvailability(itemAvailability);
        }
        availableScheduleSlots.add(reservation.getScheduleSlot());
    }
}
