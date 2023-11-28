package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
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
    @ElementCollection
    private List<Long> subItemIdList = new ArrayList<>();
    @ElementCollection
    private List<String> personalData = new ArrayList<>();
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;
    private String message;
    private Boolean confirmed;
    private ReservationStatus status;

    public ScheduleSlot getScheduleSlot() {
        return new ScheduleSlot(startDateTime, endDateTime, item.getAmount(), subItemIdList);
    }

    public void addPersonalData(int index, String data) {
        personalData.add(index, data);
    }

    public void setStatus(LocalDateTime now) {
        if (startDateTime.isBefore(now)) {
            if (status == null || status == ReservationStatus.PENDING) {
                status = ReservationStatus.UNKNOWN;
            }
        } else {
            if (!confirmed) {
                status = ReservationStatus.PENDING;
            } else {
                status = ReservationStatus.CONFIRMED;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation that)) {
            return false;
        }
        if (!user.equals(that.user)) {
            return false;
        }
        if (!item.equals(that.item)) {
            return false;
        }
        if (!Objects.equals(subItemIdList, that.subItemIdList)) {
            return false;
        }
        if (!Objects.equals(startDateTime, that.startDateTime)) {
            return false;
        }
        if (!Objects.equals(endDateTime, that.endDateTime)) {
            return false;
        }
        return amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + item.hashCode();
        result = 31 * result + (subItemIdList != null ? subItemIdList.hashCode() : 0);
        result = 31 * result + (startDateTime != null ? startDateTime.hashCode() : 0);
        result = 31 * result + (endDateTime != null ? endDateTime.hashCode() : 0);
        result = 31 * result + amount.hashCode();
        return result;
    }
}
