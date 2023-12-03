package io.github.reconsolidated.zpibackend.domain.item.response;

import io.github.reconsolidated.zpibackend.domain.reservation.dtos.ReservationDto;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class  UpdateItemFailure implements UpdateItemResponse {

    @JsonIgnore
    private static final int HTTP_RESPONSE_CODE = 403;

    private List<ReservationDto> reservations;


    @Override
    public int getHttpResponseCode() {
        return HTTP_RESPONSE_CODE;
    }
}
