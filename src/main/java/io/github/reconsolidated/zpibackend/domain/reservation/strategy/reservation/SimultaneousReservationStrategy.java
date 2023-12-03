package io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation;

import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;

import java.util.List;

public final class SimultaneousReservationStrategy implements FlexibleReservationStrategy {

    private static SimultaneousReservationStrategy instance;

    private SimultaneousReservationStrategy() {
    }

    public static SimultaneousReservationStrategy getInstance() {
        if (instance == null) {
            instance = new SimultaneousReservationStrategy();
        }
        return instance;
    }
    @Override
    public boolean verifyReservationFitting(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
        if (scheduleSlots.isEmpty()) {
            return false;
        }
        return scheduleSlots
                .stream()
                .allMatch(scheduleSlot -> scheduleSlot.getCurrAmount() >= reservationSlot.getCurrAmount());
    }

    @Override
    public boolean processReservation(Schedule schedule, Reservation reservation, List<ScheduleSlot> toReserve) {
        if (toReserve.isEmpty()) {
            return false;
        }
        for (ScheduleSlot slot : toReserve) {
            if (slot.getCurrAmount() >= reservation.getAmount()) {
                slot.setCurrAmount(slot.getCurrAmount() - reservation.getAmount());
                if (slot.getCurrAmount() == 0) {
                    schedule.removeSlot(slot);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<ScheduleSlot> processReservationDelete(Reservation reservation, List<ScheduleSlot> toReserve) {
        for (ScheduleSlot slot : toReserve) {
            slot.setCurrAmount(slot.getCurrAmount() + reservation.getAmount());
        }
        return toReserve;
    }


}
