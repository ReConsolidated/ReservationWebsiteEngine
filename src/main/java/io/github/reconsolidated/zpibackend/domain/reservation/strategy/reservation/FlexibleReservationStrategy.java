package io.github.reconsolidated.zpibackend.domain.reservation.strategy.reservation;

import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;

import java.util.List;

public interface FlexibleReservationStrategy {

    boolean verifyReservationFitting(List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot);
    boolean processReservation(Schedule schedule, Reservation reservation, List<ScheduleSlot> toReserve);
    List<ScheduleSlot> processReservationDelete(Reservation reservation, List<ScheduleSlot> toReserve);
}
