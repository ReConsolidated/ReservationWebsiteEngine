package io.github.reconsolidated.zpibackend.features.reservation.reservationData;

import java.time.LocalDateTime;

public record FlexibleReservationData(LocalDateTime start, LocalDateTime end, Integer amount) implements ReservationData {
}
