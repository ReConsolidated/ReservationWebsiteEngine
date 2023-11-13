package io.github.reconsolidated.zpibackend.features.reservation;

import java.time.LocalDateTime;

public record ScheduleSlotDto(LocalDateTime startDateTime, LocalDateTime endDateTime, ReservationType type) {

}
