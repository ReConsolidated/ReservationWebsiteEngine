package io.github.reconsolidated.zpibackend.features.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ReservationRepository  extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_Id(Long id);
    List<Reservation> findByUser_IdAndItemStoreStoreName(Long id, String name);
    List<Reservation> findByItemStoreStoreName(String name);

}
