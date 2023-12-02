package io.github.reconsolidated.zpibackend.domain.item.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private List<Availability> scheduledRanges = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDateTime;

    public ScheduleDto(Schedule schedule) {
        scheduledRanges = schedule.getAvailabilities();
    }

    public ScheduleDto(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        scheduledRanges = new ArrayList<>();
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
