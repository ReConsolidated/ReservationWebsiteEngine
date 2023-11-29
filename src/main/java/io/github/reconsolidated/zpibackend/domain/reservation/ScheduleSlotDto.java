package io.github.reconsolidated.zpibackend.domain.reservation;

import java.time.LocalDateTime;

public record ScheduleSlotDto(LocalDateTime startDateTime, LocalDateTime endDateTime, ReservationType type) {

}
