package io.github.reconsolidated.zpibackend.features.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(generator = "schedule_id_generator")
    private Long scheduleId;

    @OneToMany
    private List<ScheduleSlot> scheduleSlots;
}
