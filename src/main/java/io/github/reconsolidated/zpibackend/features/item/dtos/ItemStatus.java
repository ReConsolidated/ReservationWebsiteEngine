package io.github.reconsolidated.zpibackend.features.item.dtos;

import io.github.reconsolidated.zpibackend.features.availability.Availability;
import io.github.reconsolidated.zpibackend.features.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemStatus {
    private Availability schedule;
    private Long mark;
    private Long availableAmount;
    private LocalDateTime earliestStart;
    private LocalDateTime latestEnd;

}
