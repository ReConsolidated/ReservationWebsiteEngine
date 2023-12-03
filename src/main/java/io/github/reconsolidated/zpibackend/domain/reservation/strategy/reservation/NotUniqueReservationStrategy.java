package io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation;

import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;

import java.util.ArrayList;
import java.util.List;

public final class NotUniqueReservationStrategy implements FlexibleReservationStrategy {

    private static NotUniqueReservationStrategy INSTANCE;
    private NotUniqueReservationStrategy() {
    }

    public static NotUniqueReservationStrategy getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotUniqueReservationStrategy();
        }
        return INSTANCE;
    }

    @Override
    public boolean verifyReservationFitting(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
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

    @Override
    public boolean processReservation(Schedule schedule, Reservation reservation, List<ScheduleSlot> toReserve) {
        if (toReserve.isEmpty()) {
            return false;
        }
        List<Integer> availableSubItemsId = toReserve.get(0).getAvailableItemsIndexes();
        for (ScheduleSlot slot : toReserve) {
            availableSubItemsId = availableSubItemsId
                    .stream()
                    .filter(index -> slot.getAvailableItemsIndexes().contains(index))
                    .toList();
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
                schedule.removeSlot(slot);
            }
        }
        reservation.setSubItemIdList(reservedSubItemsIndexes);
        return true;
    }
}
