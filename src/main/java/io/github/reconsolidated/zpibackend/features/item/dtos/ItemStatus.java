package io.github.reconsolidated.zpibackend.features.item.dtos;

import io.github.reconsolidated.zpibackend.features.availability.Availability;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemStatus {
    private Availability schedule;
    private Long mark;
    private Long availableAmount;
}
