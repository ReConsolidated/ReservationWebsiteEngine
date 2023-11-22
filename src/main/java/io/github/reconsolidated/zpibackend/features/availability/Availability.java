package io.github.reconsolidated.zpibackend.features.availability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Availability {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String type;
}
