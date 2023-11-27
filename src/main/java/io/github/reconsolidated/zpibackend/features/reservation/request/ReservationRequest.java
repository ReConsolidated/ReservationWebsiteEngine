package io.github.reconsolidated.zpibackend.features.reservation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.reservation.reservationData.ReservationData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationRequest {

    @JsonProperty("storeId")
    private String storeName;
    private Long itemId;
    private AppUser userData;
    private ReservationData reservationData;
}
