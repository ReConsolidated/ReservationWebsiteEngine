package io.github.reconsolidated.zpibackend.features.reservation.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.reconsolidated.zpibackend.features.reservation.Reservation;
import io.github.reconsolidated.zpibackend.features.reservation.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {

    private Long id;
    private Long itemId;
    private List<Long> subItemIds = new ArrayList<>();
    private String userEmail;
    private Map<String,String> personalData;
    private Boolean confirmed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDateTime = startDateTime;
    private Integer amount = 1;
    private String message;
    private ReservationStatus status;

    public ReservationDto(Reservation reservation, Map<String,String> personalData) {
        this.id = reservation.getReservationId();
        this.itemId = reservation.getItem().getItemId();
        this.subItemIds = reservation.getSubItemIdList();
        this.userEmail = reservation.getUser().getEmail();
        this.personalData = personalData;
        this.confirmed = reservation.getConfirmed();
        this.startDateTime = reservation.getStartDateTime();
        this.endDateTime = reservation.getEndDateTime();
        this.amount = reservation.getAmount();
        this.message = reservation.getMessage();
        reservation.setStatus(LocalDateTime.now().atZone(ZoneId.of("UTC")).toLocalDateTime());
        this.status = reservation.getStatus();
    }
}
