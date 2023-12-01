package io.github.reconsolidated.zpibackend.domain.reservation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityRequestUnique {

    private Long itemId;
    private Integer amount;
}
