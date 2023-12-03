package io.github.reconsolidated.zpibackend.domain.reservation.strategy.time;

import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContinuousTimeStrategy implements FlexibleTimeStrategy {

    private static ContinuousTimeStrategy INSTANCE;
    private ContinuousTimeStrategy() {
    }

    public static ContinuousTimeStrategy getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ContinuousTimeStrategy();
        }
        return INSTANCE;
    }
    @Override
    public boolean verifyScheduleSlots(Schedule schedule, List<ScheduleSlot> scheduleSlots, ScheduleSlot reservationSlot) {
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

    @Override
    public List<ScheduleSlot> prepareSchedule(Schedule schedule, Reservation reservation, List<ScheduleSlot> toReserve) {
        if (toReserve.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ScheduleSlot> preparedToReserve = new ArrayList<>(toReserve.size());
        preparedToReserve.addAll(toReserve);

        if (preparedToReserve.get(0).getStartDateTime().isBefore(reservation.getStartDateTime())) {
            ScheduleSlot first = preparedToReserve.get(0);
            int firstIndex = schedule.getAvailableScheduleSlots().indexOf(first);
            schedule.getAvailableScheduleSlots().remove(firstIndex);
            preparedToReserve.remove(0);
            ScheduleSlot[] split = first.split(reservation.getStartDateTime());
            schedule.getAvailableScheduleSlots().addAll(firstIndex, Arrays.asList(split));
            preparedToReserve.add(0, split[1]);
        }
        if (preparedToReserve.get(preparedToReserve.size() - 1).getEndDateTime().isAfter(reservation.getEndDateTime())) {
            ScheduleSlot last = preparedToReserve.get(preparedToReserve.size() - 1);
            int lastIndex = schedule.getAvailableScheduleSlots().indexOf(last);
            schedule.getAvailableScheduleSlots().remove(lastIndex);
            preparedToReserve.remove(preparedToReserve.size() - 1);
            ScheduleSlot[] split = last.split(reservation.getEndDateTime());
            schedule.getAvailableScheduleSlots().addAll(lastIndex, Arrays.asList(split));
            preparedToReserve.add(split[0]);
        }
        return preparedToReserve;
    }
}
