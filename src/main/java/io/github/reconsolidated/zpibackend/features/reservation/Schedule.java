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
    private List<ScheduleSlot> availableScheduleSlots; //it must be sorted, and not overlapping

    public Schedule(Long scheduleId, Item item) {

        this.scheduleId = scheduleId;
        this.item = item;
        this.availableScheduleSlots = new ArrayList<>();

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
                            scheduleSlot.getStartDateTime(), scheduleSlot.getAmount(), ReservationType.MORNING);
                    availableScheduleSlots.add(index, morningSlot);
                    scheduleSlot.setType(ReservationType.CONTINUOUS);
                } else if (daySlots.size() == 1 && daySlots.get(0).getType() == ReservationType.MORNING) {
                    scheduleSlot.setType(ReservationType.OVERNIGHT);
                    int index = availableScheduleSlots.indexOf(daySlots.get(0));
                    availableScheduleSlots.remove(index);
                    availableScheduleSlots.addAll(index,
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
                List<ScheduleSlot> nextDaySlots = availableScheduleSlots.stream()
                        .filter(slot ->
                                slot.getStartDateTime().getYear() == scheduleSlot.getStartDateTime().getYear() && slot.getStartDateTime().getDayOfYear() == scheduleSlot.getStartDateTime().getDayOfYear() + 1)
                        .toList();
                if (nextDaySlots.isEmpty()) {
                    availableScheduleSlots.add(new ScheduleSlot(scheduleSlot.getStartDateTime().plusDays(1),
                            scheduleSlot.getStartDateTime().plusDays(1), scheduleSlot.getAmount(), ReservationType.MORNING));
                } else {
                    availableScheduleSlots.add(new ScheduleSlot(nextDaySlots.get(0).getStartDateTime(),
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

        if (availableScheduleSlots.isEmpty()) {
            return false;
        }
        availableScheduleSlots.sort(Comparator.comparing(ScheduleSlot::getStartDateTime));

        boolean verified = false;

        List<ScheduleSlot> toVerify = new ArrayList<>(availableScheduleSlots.stream()
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

        if (availableScheduleSlots.isEmpty()) {
            return false;
        }

        List<ScheduleSlot> toVerify = availableScheduleSlots.stream()
                .filter(slot -> slot.equalsTime(scheduleSlot))
                .toList();

        return toVerify.size() == 1 && toVerify.get(0).getAmount() >= scheduleSlot.getAmount();
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
            slot.setAmount(slot.getAmount() - reservation.getAmount());
            for (int i = 0; i < reservation.getAmount(); i++) {
                slot.getItemsAvailability().set(availableItems.get(i), false);
            }
            if (slot.getAmount() == 0) {
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

    public ArrayList<ScheduleSlot> suggest(ScheduleSlot originalSlot){

        List<ScheduleSlot> fittingDaySlots = availableScheduleSlots.stream()
                .filter(slot ->
                        slot.getStartDateTime().getYear() == originalSlot.getStartDateTime().getYear() &&
                        slot.getStartDateTime().getDayOfYear() == originalSlot.getStartDateTime().getDayOfYear() &&
                        slot.getAmount() >= originalSlot.getAmount())
                .toList();
        //same day suggestions
        ArrayList<ScheduleSlot> suggestions = new ArrayList<>(
                getLongestSlotsWithAmount(originalSlot.getAmount(), fittingDaySlots));

        ScheduleSlot dayBeforeSlot = ScheduleSlot.builder()
                .amount(originalSlot.getAmount())
                .startDateTime(originalSlot.getStartDateTime().minusDays(1))
                .endDateTime(originalSlot.getEndDateTime().minusDays(1))
                .build();

        return suggestions;
    }

    private ArrayList<ScheduleSlot> getLongestSlotsWithAmount(int amount, List<ScheduleSlot> scheduleSlots) {
        //result list of longest found slots that have sufficient amount
        ArrayList<ScheduleSlot> longestSlots = new ArrayList<>();
        //getting rid of slots with to low amount to optimize search
        List<ScheduleSlot> slotsToCheck = scheduleSlots.stream().filter(slot -> slot.getAmount() >= amount).toList();
        //slot can't have a gap inside, so we only have to search through continuous groups of slots
        ArrayList<ArrayList<ScheduleSlot>> continuousSlotsLists = getContinuousSlotsLists(slotsToCheck);

        //for each continuous group of slots
        for(ArrayList<ScheduleSlot> continuousSlots : continuousSlotsLists) {
            //each slot can be a start of the longest possible slot
            for(ScheduleSlot slot : continuousSlots) {
                ScheduleSlot longestSlot = ScheduleSlot.builder()
                        .startDateTime(slot.getStartDateTime())
                        .amount(slot.getAmount())
                        .build();
                //index of current first slot in continuous group of slots
                int startIndex = continuousSlots.indexOf(slot);
                //list of items that are continuously available
                ArrayList<Integer> subItemIndexes= slot.getAvailableItemsIndexes();
                //flag for optimization
                // if is true we don't have to check later slots because they are used in the longest possible slot
                boolean coverAllSlots = true;
                for (int i = startIndex + 1; i < continuousSlots.size(); i ++) {

                    ScheduleSlot slotToCheck = continuousSlots.get(i);
                    boolean insufficientAmount = false;
                    //checking availability of each sub item
                    for (int j = 0; j < slotToCheck.getItemsAvailability().size(); j++) {
                        if (subItemIndexes.contains(j) && !slotToCheck.getItemsAvailability().get(j)) {
                            if (subItemIndexes.size() - 1 >= amount) {
                                subItemIndexes.remove(Integer.valueOf(j));
                            } else {
                                //slot doesn't have enough continuous sub items, so we'll have to end slot before it
                                insufficientAmount = true;
                                coverAllSlots = false;
                                break;
                            }
                        }
                    }
                    //ending slot
                    if (insufficientAmount) {
                        longestSlot.setEndDateTime(continuousSlots.get(i - 1).getEndDateTime());
                        longestSlots.add(longestSlot);
                        break;
                    }
                }
                //optimization if we have the longest possible slot containing all left slots in group
                if(coverAllSlots) {
                    break;
                }
            }
        }
        return longestSlots;
    }

    private ArrayList<ArrayList<ScheduleSlot>> getContinuousSlotsLists(List<ScheduleSlot> scheduleSlots) {
        if(scheduleSlots.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ArrayList<ScheduleSlot>> continuousSlots = new ArrayList<>();
        ArrayList<ScheduleSlot> currList = new ArrayList<>();
        ScheduleSlot prev = scheduleSlots.get(0);

        for(ScheduleSlot slot: scheduleSlots) {
            if (prev.isContinuousWith(slot) || prev.equalsTime(slot)) {
                currList.add(slot);
            } else {
                continuousSlots.add(currList);
                currList = new ArrayList<>();
                currList.add(slot);
            }
            prev = slot;
        }
        return continuousSlots;
    }
}
