package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(generator = "reservation_generator")
    private Long reservationId;
    @ManyToOne
    private AppUser user;
    @ManyToOne
    private Item item;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;
    private Integer places;
    private String message;
    private Boolean confirmed;

    public ScheduleSlot getScheduleSlot() {

        return ScheduleSlot.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .amount(amount)
                .capacity(places)
                .build();
    }

}
