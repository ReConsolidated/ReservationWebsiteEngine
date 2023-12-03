package io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation;

import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;

import java.util.List;

public final class BasicReservationStrategy implements FlexibleReservationStrategy {

    private static BasicReservationStrategy INSTANCE;

    private BasicReservationStrategy() {
    }

    public static BasicReservationStrategy getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BasicReservationStrategy();
        }
        return INSTANCE;
    }
    @Override
    public boolean verifyReservationFitting(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
        return true;
    }

    @Override
    public boolean processReservation(Schedule schedule, Reservation reservation, List<ScheduleSlot> toReserve) {
        return SimultaneousReservationStrategy.getInstance().processReservation(schedule, reservation, toReserve);
    }
}
