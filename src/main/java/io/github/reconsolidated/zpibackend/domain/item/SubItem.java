package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.item.dtos.SubItemDto;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;
import lombok.*;

import javax.persistence.*;

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
    @OneToOne(cascade = CascadeType.ALL)
    private ScheduleSlot slot;
    private Integer amount;

    public SubItemDto toSubItemDto() {
        return new SubItemDto(subItemId, title, subtitle);
    }

}
