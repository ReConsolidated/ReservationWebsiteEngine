package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubItem {

    @Id
    @GeneratedValue(generator = "sub_item_generator")
    private Long subItemId;
    private String title;
    private String subtitle;
    @OneToOne
    private ScheduleSlot slot;
    private Integer capacity;
    private Integer amount;

}
