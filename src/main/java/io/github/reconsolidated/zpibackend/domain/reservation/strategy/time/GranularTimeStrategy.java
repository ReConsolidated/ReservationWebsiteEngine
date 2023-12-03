package io.github.reconsolidated.zpibackend.domain.reservation.strategy.time;

import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;

import java.util.List;

public class GranularTimeStrategy implements FlexibleTimeStrategy {

    private static GranularTimeStrategy INSTANCE;

    private GranularTimeStrategy() {
    }

    public static GranularTimeStrategy getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GranularTimeStrategy();
        }
        return INSTANCE;
    }
    @Override
    public boolean verifyScheduleSlots(Schedule schedule, List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
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

    @Override
    public List<ScheduleSlot> prepareSchedule(Schedule schedule, Reservation reservation, List<ScheduleSlot> toReserve) {
        return toReserve;
    }
}
