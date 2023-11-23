package io.github.reconsolidated.zpibackend.features.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubItem {

    @Id
    @JsonDeserialize(as = Long.class)
    @GeneratedValue(generator = "sub_item_generator")
    private Long subItemId;
    private String title;
    private String subtitle;
    @OneToOne
    private ScheduleSlot slot;
    private Integer amount;

}
