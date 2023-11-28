package io.github.reconsolidated.zpibackend.features.item.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.reconsolidated.zpibackend.features.availability.Availability;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
