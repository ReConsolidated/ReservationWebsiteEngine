package io.github.reconsolidated.zpibackend.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository  extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_Id(Long id);
    List<Reservation> findByUser_IdAndItemStoreStoreName(Long id, String name);
    List<Reservation> findByItemStoreStoreName(String name);

}
