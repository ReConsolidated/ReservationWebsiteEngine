package io.github.reconsolidated.zpibackend.features.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ReservationRepository  extends JpaRepository<Reservation, Long> {
}
