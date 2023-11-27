package io.github.reconsolidated.zpibackend.features.reservation.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityResponseSuccess implements CheckAvailabilityResponse {

    private static final int RESPONSE_CODE = 200;
    private Long itemId;
    private Integer amount = 1;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Override
    public int getResponseCode() {
        return RESPONSE_CODE;
    }
}
