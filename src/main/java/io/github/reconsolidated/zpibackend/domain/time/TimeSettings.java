package io.github.reconsolidated.zpibackend.domain.time;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TimeSettings {
    @Id
    @GeneratedValue(generator = "time_settings_id_generator")
    private Long id;
}
