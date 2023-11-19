package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityResponseSuggestion implements CheckAvailabilityResponse {

    private static final int RESPONSE_CODE = 203;
    private Long itemId;
    private Integer amount = 1;
    private List<ScheduleSlot> schedule;
    private LocalDateTime suggestedStart;
    private LocalDateTime suggestedEnd;

    @Override
    public int getResponseCode() {
        return RESPONSE_CODE;
    }
}
