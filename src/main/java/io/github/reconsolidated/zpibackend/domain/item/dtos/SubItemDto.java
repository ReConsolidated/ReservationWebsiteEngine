package io.github.reconsolidated.zpibackend.domain.item.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubItemDto {
    private Long id;
    private String title;
    private String subtitle;
    private Integer amount;
    private ScheduleDto schedule;

}
