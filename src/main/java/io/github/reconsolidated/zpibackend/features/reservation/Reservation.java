package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @OneToOne
    private ScheduleSlot scheduleSlot;
    private Integer amount;
    private Integer places;

}
