package io.github.reconsolidated.zpibackend.features.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository  extends JpaRepository<Reservation, Long> {
}
