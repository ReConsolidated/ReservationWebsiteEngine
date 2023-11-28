package io.github.reconsolidated.zpibackend.features.reservation.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.reconsolidated.zpibackend.features.item.dtos.SubItemDto;
import io.github.reconsolidated.zpibackend.features.reservation.Reservation;
import io.github.reconsolidated.zpibackend.features.reservation.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReservationDto {

    private Long reservationId;
    private SubItemDto item;
    private List<SubItemDto> subItems = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDateTime = startDateTime;
    private Integer amount = 1;
    private String message;
    private Boolean confirmed;
    private ReservationStatus status;

    public UserReservationDto(Reservation reservation) {

    }
}
