package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.item.dtos.ScheduleDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemInfoDto;
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
    @Builder.Default
    private Integer amount = 1;

    public SubItem(SubItemDto subItemDto) {
        this.subItemId = subItemDto.getSubItemId();
        this.title = subItemDto.getTitle();
        this.subtitle = subItemDto.getSubtitle();
        this.amount = subItemDto.getAmount();
        if (subItemDto.getSchedule() != null) {
            this.startDateTime = subItemDto.getSchedule().getStartDateTime();
            this.endDateTime = subItemDto.getSchedule().getEndDateTime() == null ?
                    subItemDto.getSchedule().getStartDateTime() :
                    subItemDto.getSchedule().getEndDateTime();
        }
    }

    public ScheduleSlot getSlot() {
        return new ScheduleSlot(startDateTime, endDateTime, amount);
    }
    public SubItemDto toSubItemDto() {
        return new SubItemDto(subItemId, title, subtitle, amount, new ScheduleDto(startDateTime, endDateTime));
    }
    public SubItemInfoDto toSubItemInfoDto() {
        return new SubItemInfoDto(subItemId, title, subtitle);
    }


}
