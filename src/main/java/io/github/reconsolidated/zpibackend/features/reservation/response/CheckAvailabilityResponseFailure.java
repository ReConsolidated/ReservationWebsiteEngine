package io.github.reconsolidated.zpibackend.features.reservation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityResponseFailure implements CheckAvailabilityResponse {

    private static final int RESPONSE_CODE = 204;
    private Long itemId;
    private Integer amount = 1;

    @Override
    public int getResponseCode() {
        return RESPONSE_CODE;
    }
}
