package io.github.reconsolidated.zpibackend.features.reservation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @JsonDeserialize(as = Long.class)
    @GeneratedValue(generator = "reservation_generator")
    private Long reservationId;
    @ManyToOne
    private AppUser user;
    @ManyToOne
    private Item item;
    @ElementCollection
    private List<Long> subItemIdList;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;
    private String message;
    private Boolean confirmed;

    public ScheduleSlot getScheduleSlot() {

        return new ScheduleSlot(startDateTime, endDateTime, amount);
    }

}
