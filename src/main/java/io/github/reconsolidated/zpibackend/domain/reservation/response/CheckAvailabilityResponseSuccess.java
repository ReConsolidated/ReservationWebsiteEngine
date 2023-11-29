package io.github.reconsolidated.zpibackend.domain.reservation.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDate;

    @Override
    public int getResponseCode() {
        return RESPONSE_CODE;
    }
}
