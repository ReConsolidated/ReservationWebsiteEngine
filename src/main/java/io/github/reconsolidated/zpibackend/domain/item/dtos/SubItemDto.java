package io.github.reconsolidated.zpibackend.domain.item.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubItemDto implements Comparable<SubItemDto>{
    private Long id;
    private String title;
    private String subtitle;
    private Integer amount;
    private Integer availableAmount;
    private ScheduleDto schedule;

    @Override
    public int compareTo(@NotNull SubItemDto subItemDto) {
        if(id == null) {
            return 1;
        }
        return id.compareTo(subItemDto.getId());
    }
}
