package io.github.reconsolidated.zpibackend.features.reservation.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.reconsolidated.zpibackend.features.availability.Availability;
import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityResponseSuggestion implements CheckAvailabilityResponse {

    private static final int RESPONSE_CODE = 203;
    private Integer id;
    private Long itemId;
    private Integer amount;
    private List<Availability> schedule;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime suggestedStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime suggestedEnd;

    public CheckAvailabilityResponseSuggestion(Integer id, Long itemId, ScheduleSlot slot, List<Availability> availabilities) {
        this.id = id;
        this.itemId = itemId;
        this.schedule = availabilities;
        this.suggestedStart = slot.getStartDateTime();
        this.suggestedEnd = slot.getEndDateTime();
        this.amount = slot.getCurrAmount();
    }

    @Override
    public int getResponseCode() {
        return RESPONSE_CODE;
    }
}
