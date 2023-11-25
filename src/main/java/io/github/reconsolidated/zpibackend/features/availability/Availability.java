package io.github.reconsolidated.zpibackend.features.availability;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Availability {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String type;
}
