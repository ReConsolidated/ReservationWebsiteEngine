package io.github.reconsolidated.zpibackend.features.reservation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityRequest {

    private Long itemId;
    private Integer amount = 1;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
