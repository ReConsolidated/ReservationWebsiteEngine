package io.github.reconsolidated.zpibackend.domain.reservation;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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
    private String email;
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
        if (status == ReservationStatus.CANCELLED_BY_USER || status == ReservationStatus.CANCELLED_BY_ADMIN) {

        } else {
            if (startDateTime == null || !startDateTime.isBefore(now)) {
                status = ReservationStatus.ACTIVE;
            } else {
                status = ReservationStatus.PAST;
            }
        }
    }

    public Map<String, String> getPersonalDataMap() {
        Map<String, String> personalDataMap = new HashMap<>();
        for (int i = 0; i < item.getStore().getStoreConfig().getAuthConfig().getRequiredPersonalData().size(); i++) {
            personalDataMap.put(item.getStore().getStoreConfig().getAuthConfig().getRequiredPersonalData().get(i),
                    personalData.get(i));
        }
        return personalDataMap;
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

    public void setStatus(ReservationStatus reservationStatus) {
        this.status = reservationStatus;
    }
}
