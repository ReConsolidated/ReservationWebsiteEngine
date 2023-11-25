package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
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

}
