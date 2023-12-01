package io.github.reconsolidated.zpibackend.domain.reservation.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemInfoDto;
import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReservationDto {

    private Long reservationId;
    private SubItemInfoDto item;
    private List<SubItemInfoDto> subItems = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDateTime = startDateTime;
    private Integer amount = 1;
    private String message;
    private Boolean confirmed;
    private ReservationStatus status;

    public UserReservationDto(Reservation reservation, List<SubItemInfoDto> subItems) {
        this.reservationId = reservation.getReservationId();
        this.item = reservation.getItem().toSubItemDto();
        this.subItems = subItems;
        this.confirmed = reservation.getConfirmed();
        this.startDateTime = reservation.getStartDateTime();
        this.endDateTime = reservation.getEndDateTime();
        this.amount = reservation.getAmount();
        this.message = reservation.getMessage();
        reservation.setStatus(LocalDateTime.now().atZone(ZoneId.of("UTC")).toLocalDateTime());
        this.status = reservation.getStatus();

    }
}
