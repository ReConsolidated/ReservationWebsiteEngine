package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemDto;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubItem {

    @Id
    @GeneratedValue(generator = "sub_item_generator")
    private Long subItemId;
    private String title;
    private String subtitle;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;

    public ScheduleSlot getSlot() {
        return new ScheduleSlot(startDateTime, endDateTime, amount);
    }
    public SubItemDto toSubItemDto() {
        return new SubItemDto(subItemId, title, subtitle);
    }

}
