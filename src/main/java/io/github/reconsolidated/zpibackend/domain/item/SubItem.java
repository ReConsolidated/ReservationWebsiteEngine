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
    @JoinColumn(name = "item_id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Item item;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    @Builder.Default
    private Integer amount = 1;
    @Builder.Default
    private Integer initialAmount = 1;

    public SubItem(SubItemDto subItemDto, Item item) {
        this.subItemId = subItemDto.getId();
        this.item = item;
        this.title = subItemDto.getTitle();
        this.subtitle = subItemDto.getSubtitle();
        this.initialAmount = subItemDto.getInitialAmount() == null ? 1 : subItemDto.getInitialAmount();
        this.amount = subItemDto.getAmount() == null ? this.initialAmount : subItemDto.getAmount();
        if (subItemDto.getSchedule() != null) {
            this.startDateTime = subItemDto.getSchedule().getStartDateTime();
            this.endDateTime = subItemDto.getSchedule().getEndDateTime() == null ?
                    subItemDto.getSchedule().getStartDateTime() :
                    subItemDto.getSchedule().getEndDateTime();
        } else {
            this.startDateTime = item.getInitialSchedule().getAvailableScheduleSlots().get(0).getStartDateTime();
            this.endDateTime = item.getInitialSchedule().getAvailableScheduleSlots().get(0).getEndDateTime();
        }
    }
    public ScheduleSlot getSlot() {
        return new ScheduleSlot(startDateTime, endDateTime, amount);
    }
    public SubItemDto toSubItemDto() {
        return new SubItemDto(
                subItemId,
                title,
                subtitle,
                initialAmount,
                amount,
                new ScheduleDto(startDateTime, endDateTime));
    }
    public SubItemInfoDto toSubItemInfoDto() {
        return new SubItemInfoDto(subItemId, title, subtitle);
    }


}
