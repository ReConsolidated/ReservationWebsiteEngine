package io.github.reconsolidated.zpibackend.application;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.reservation.request.CheckAvailabilityRequestUnique;
import io.github.reconsolidated.zpibackend.infrastracture.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationService;
import io.github.reconsolidated.zpibackend.domain.reservation.dtos.ReservationDto;
import io.github.reconsolidated.zpibackend.domain.reservation.dtos.UserReservationDto;
import io.github.reconsolidated.zpibackend.domain.reservation.request.CheckAvailabilityRequest;
import io.github.reconsolidated.zpibackend.domain.reservation.response.CheckAvailabilityResponse;
import lombok.AllArgsConstructor;
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

        List<CheckAvailabilityResponse> response = reservationService.checkAvailabilityNotUnique(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refetch")
    public ResponseEntity<?> reFetchScheduleUnique(@CurrentUser AppUser currentUser,
                                             @PathVariable String storeName,
                                             @RequestBody CheckAvailabilityRequestUnique request) {

        return ResponseEntity.ok(reservationService.checkAvailabilityUnique(request));
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@CurrentUser AppUser currentUser,
                                     @PathVariable String storeName,
                                     @RequestBody ReservationDto reservation) {

        return ResponseEntity.ok(reservationService.reserveItem(currentUser, reservation));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@CurrentUser AppUser currentUser, @PathVariable Long reservationId) {
        reservationService.deleteReservation(currentUser, reservationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserReservationDto>> userReservations(@CurrentUser AppUser currentUser,
                                                                     @PathVariable String storeName) {
        return ResponseEntity.ok(reservationService.getUserReservationsDto(currentUser.getId(), storeName));
    }

    @GetMapping
    public ResponseEntity<List<ReservationDto>> storeReservations(@CurrentUser AppUser currentUser,
                                                                  @PathVariable String storeName) {
        return ResponseEntity.ok(reservationService.getStoreReservations(currentUser, storeName));
    }
}
