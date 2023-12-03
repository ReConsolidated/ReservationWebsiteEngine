package io.github.reconsolidated.zpibackend.domain.reservation.strategy.time;

import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationType;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;

import java.util.List;

public interface FlexibleTimeStrategy {

    boolean verifyScheduleSlots(Schedule schedule, List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot);

    List<ScheduleSlot> prepareSchedule(Schedule schedule, Reservation reservation, List<ScheduleSlot> toReserve);

    default boolean checkSlotsContinuity(List<ScheduleSlot> scheduleSlots) {
        for (int i = 0; i < scheduleSlots.size() - 1; i++) {
            if (!scheduleSlots.get(i).getEndDateTime().equals(scheduleSlots.get(i + 1).getStartDateTime()) &&
                    !(scheduleSlots.get(i).getType() == ReservationType.OVERNIGHT &&
                            scheduleSlots.get(i + 1).getType() == ReservationType.MORNING)) {
                return false;
            }
        }
        return true;
    }
}
