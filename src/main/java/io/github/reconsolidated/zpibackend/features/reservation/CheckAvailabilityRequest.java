package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CheckAvailabilityRequest {

    private final Long itemId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private Integer amount = 1;
    private Integer places = 1;

}
