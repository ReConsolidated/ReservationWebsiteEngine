package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.features.reservation.request.CheckAvailabilityRequest;
import io.github.reconsolidated.zpibackend.features.reservation.response.CheckAvailabilityResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/stores/{storeName}/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/check")
    public ResponseEntity<?> reFetchSchedule(@CurrentUser AppUser currentUser,
                                             @PathVariable String storeName,
                                             @RequestBody CheckAvailabilityRequest request) {

        CheckAvailabilityResponse response = reservationService.checkAvailability(request);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResponseCode()));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@CurrentUser AppUser currentUser, @PathVariable Long reservationId) {
        reservationService.deleteReservation(currentUser, reservationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<Reservation>> userReservations(@CurrentUser AppUser currentUser,
                                                              @PathVariable String storeName) {
        return ResponseEntity.ok(reservationService.getUserReservations(currentUser.getId(), storeName));
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> storeReservations(@CurrentUser AppUser currentUser,
                                                              @PathVariable String storeName) {
        return ResponseEntity.ok(reservationService.getStoreReservations(currentUser, storeName));
    }
}
