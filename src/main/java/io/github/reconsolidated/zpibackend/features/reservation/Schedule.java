package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.features.availability.Availability;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import lombok.*;

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
    @OneToOne
    private Item item;
    @OneToMany
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
                        slot.getType().toString()
                )).collect(Collectors.toList());
    }

    public void addSlot(ScheduleSlot scheduleSlot) {

        setSlotType(scheduleSlot);

        for (int i = 0; i < availableScheduleSlots.size(); i++) {

            if (scheduleSlot.startsEarlierThan(availableScheduleSlots.get(i))) {
                //slot is before next slot, so it is this slot place
                if (!scheduleSlot.getEndDateTime().isAfter(availableScheduleSlots.get(i).getStartDateTime()) &&
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
        } else if (core.getIsAllowOvernight()) {
            //slots available on same day as new scheduleSlot
            List<ScheduleSlot> daySlots = availableScheduleSlots.stream()
                    .filter(slot ->
                            slot.getStartDateTime().getYear() == scheduleSlot.getStartDateTime().getYear() &&
                            slot.getStartDateTime().getDayOfYear() == scheduleSlot.getStartDateTime().getDayOfYear())
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
                    ScheduleSlot morningSlot = new ScheduleSlot(scheduleSlot.getStartDateTime(),
                            scheduleSlot.getStartDateTime(), scheduleSlot.getCurrAmount(), ReservationType.MORNING);
                    availableScheduleSlots.add(index, morningSlot);
                    scheduleSlot.setType(ReservationType.CONTINUOUS);
                } else if (daySlots.size() == 1 && daySlots.get(0).getType() == ReservationType.MORNING) {
                    scheduleSlot.setType(ReservationType.OVERNIGHT);
                    int index = availableScheduleSlots.indexOf(daySlots.get(0));
                    availableScheduleSlots.remove(index);
                    availableScheduleSlots.addAll(index,
                            Arrays.asList(new ScheduleSlot(scheduleSlot.getStartDateTime(),
                                            scheduleSlot.getStartDateTime(), scheduleSlot.getCurrAmount(), ReservationType.MORNING),
                                    new ScheduleSlot(scheduleSlot.getStartDateTime().plusDays(1),
                                            scheduleSlot.getStartDateTime().plusDays(1), scheduleSlot.getCurrAmount(), ReservationType.MORNING)));
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
                List<ScheduleSlot> nextDaySlots = availableScheduleSlots.stream()
                        .filter(slot ->
                                slot.getStartDateTime().getYear() == scheduleSlot.getStartDateTime().getYear() && slot.getStartDateTime().getDayOfYear() == scheduleSlot.getStartDateTime().getDayOfYear() + 1)
                        .toList();
                if (nextDaySlots.isEmpty()) {
                    availableScheduleSlots.add(new ScheduleSlot(scheduleSlot.getStartDateTime().plusDays(1),
                            scheduleSlot.getStartDateTime().plusDays(1), scheduleSlot.getCurrAmount(), ReservationType.MORNING));
                } else {
                    availableScheduleSlots.add(new ScheduleSlot(nextDaySlots.get(0).getStartDateTime(),
                                    nextDaySlots.get(0).getStartDateTime(), scheduleSlot.getCurrAmount(), ReservationType.MORNING));
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

        if (availableScheduleSlots.isEmpty()) {
            return false;
        }
        availableScheduleSlots.sort(Comparator.comparing(ScheduleSlot::getStartDateTime));

        List<ScheduleSlot> toVerify = new ArrayList<>(availableScheduleSlots.stream()
                .filter(slot -> slot.overlap(scheduleSlot))
                .toList());
        if (toVerify.isEmpty()) {
            return false;
        }
        LocalDateTime prevSlotEndTime = toVerify.get(0).getStartDateTime();

        if (!prevSlotEndTime.isAfter(scheduleSlot.getStartDateTime())) {
            ArrayList<Integer> subItemIndexes = toVerify.get(0).getAvailableItemsIndexes();
            for (ScheduleSlot currSlot : toVerify) {
                //remove object
                subItemIndexes.removeIf(index -> !currSlot.getAvailableItemsIndexes().contains(index));
                //gap between slots or amount is too small
                if (currSlot.getStartDateTime().isAfter(prevSlotEndTime) ||
                        subItemIndexes.size() < scheduleSlot.getCurrAmount()) {
                    return false;
                }
                //whole schedule slot is covered by slots in schedule
                if (!currSlot.getEndDateTime().isBefore(scheduleSlot.getEndDateTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verifyGranular(ScheduleSlot scheduleSlot) {

        if (availableScheduleSlots.isEmpty()) {
            return false;
        }

        List<ScheduleSlot> toVerify = availableScheduleSlots.stream()
                .filter(slot -> slot.equalsTime(scheduleSlot))
                .toList();

        return toVerify.size() == 1 && toVerify.get(0).getCurrAmount() >= scheduleSlot.getCurrAmount();
    }


    /**
     Use verify method before calling processReservation()
     */
    public void processReservation(Reservation reservation) {

        List<ScheduleSlot> toReserve = new ArrayList<>(availableScheduleSlots.stream()
                .filter(slot -> slot.overlap(reservation.getScheduleSlot()))
                .toList());

        List<Integer> availableItems = findAvailableItem(toReserve);
        if (availableItems.size() < reservation.getAmount()) {
            throw new IllegalArgumentException();
        }
        if (toReserve.get(0).getStartDateTime().isBefore(reservation.getStartDateTime())) {
            ScheduleSlot first = toReserve.get(0);
            toReserve.remove(0);
            int scheduleIndex = availableScheduleSlots.indexOf(first);
            availableScheduleSlots.remove(scheduleIndex);
            ScheduleSlot[] split = first.split(reservation.getStartDateTime());
            setSlotType(split[0]);
            setSlotType(split[1]);
            availableScheduleSlots.addAll(scheduleIndex, Arrays.asList(split));
            toReserve.add(0, split[1]);
        }
        if (toReserve.get(toReserve.size() - 1).getEndDateTime().isAfter(reservation.getEndDateTime())) {
            ScheduleSlot last = toReserve.get(toReserve.size() - 1);
            toReserve.remove(toReserve.size() - 1);
            int scheduleIndex = availableScheduleSlots.indexOf(last);
            availableScheduleSlots.remove(scheduleIndex);
            ScheduleSlot[] split = last.split(reservation.getEndDateTime());
            setSlotType(split[0]);
            setSlotType(split[1]);
            availableScheduleSlots.addAll(scheduleIndex, Arrays.asList(split));
            toReserve.add(split[0]);
        }

        for (ScheduleSlot slot: toReserve) {
            slot.setCurrAmount(slot.getCurrAmount() - reservation.getAmount());
            for (int i = 0; i < reservation.getAmount(); i++) {
                slot.getItemsAvailability().set(availableItems.get(i), false);
            }
            if (slot.getCurrAmount() == 0) {
                availableScheduleSlots.remove(slot);
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

    /**
     Returns list of slots that are suggested based on given slot.
     Suggestions can be categorised into:
     - the longest possible slots on the same day and with same amount as original slot
     - if free slot in same time but day before
     - if free slot in same time but day after
     - if free slot in same time but week after
     */
    public List<ScheduleSlot> suggest(ScheduleSlot originalSlot) {

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
        if (verify(false, dayBeforeSlot)) {
            suggestions.add(dayBeforeSlot);
        }
        ScheduleSlot dayAfterSlot = new ScheduleSlot(
                originalSlot.getStartDateTime().plusDays(1),
                originalSlot.getEndDateTime().plusDays(1),
                item.getAmount());
        if (verify(false, dayAfterSlot)) {
            suggestions.add(dayAfterSlot);
        }
        ScheduleSlot weekAfterSlot = new ScheduleSlot(
                originalSlot.getStartDateTime().plusDays(WEEKDAYS),
                originalSlot.getEndDateTime().plusDays(WEEKDAYS),
                item.getAmount());
        if (verify(false, weekAfterSlot)) {
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
}
