package io.github.reconsolidated.zpibackend.domain.reservation.response;

import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityResponseUnique {

    private Long itemId;
    private Integer amount;
    private List<Availability> schedule;
}
