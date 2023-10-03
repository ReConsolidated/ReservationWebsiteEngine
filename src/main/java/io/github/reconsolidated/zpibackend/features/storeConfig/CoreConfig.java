package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreConfig {
    @Id
    @GeneratedValue(generator = "core_config_generator")
    private Long coreConfigId;
    private Boolean simultaneous;
    private Boolean uniqueness;
    private Boolean flexibility;
    private Boolean granularity;
    private Boolean periodicity;
    private Boolean specificReservation;
}
