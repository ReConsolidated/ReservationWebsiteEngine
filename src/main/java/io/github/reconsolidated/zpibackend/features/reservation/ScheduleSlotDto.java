package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.features.reservation.ReservationType;

import java.time.LocalDateTime;

public record ScheduleSlotDto(LocalDateTime startDateTime, LocalDateTime endDateTime, ReservationType type) {

}
