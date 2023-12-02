package io.github.reconsolidated.zpibackend.domain.item.response;

import io.github.reconsolidated.zpibackend.domain.reservation.dtos.ReservationDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class  UpdateItemFailure implements UpdateItemResponse {

    private List<ReservationDto> reservations;
}
