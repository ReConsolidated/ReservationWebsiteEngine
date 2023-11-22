package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/check")
    public ResponseEntity<?> reFetchSchedule(@CurrentUser AppUser currentUser, @RequestBody CheckAvailabilityRequest request) {

        CheckAvailabilityResponse response = reservationService.checkAvailability(request);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResponseCode()));
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> userReservations(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(reservationService.getReservations(currentUser.getId()));
    }
}
