package io.github.reconsolidated.zpibackend.domain.reservation.dtos;

import io.github.reconsolidated.zpibackend.domain.reservation.ReservationType;

import java.time.LocalDateTime;

public record ScheduleSlotDto(LocalDateTime startDateTime, LocalDateTime endDateTime, ReservationType type) {

}
