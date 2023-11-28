package io.github.reconsolidated.zpibackend.domain.storeConfig;

import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CoreConfig {
    @Id
    @GeneratedValue(generator = "core_config_generator")
    private Long coreConfigId;
    private Boolean flexibility = false;
    private Boolean granularity = false;
    private Boolean simultaneous = false;
    private Boolean uniqueness = false;
    private Boolean periodicity = false;
    private Boolean specificReservation = false;
    private Boolean isAllowOvernight = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoreConfig that)) {
            return false;
        }
        return Objects.equals(coreConfigId, that.coreConfigId) &&
                Objects.equals(simultaneous, that.simultaneous) &&
                Objects.equals(uniqueness, that.uniqueness) &&
                Objects.equals(flexibility, that.flexibility) &&
                Objects.equals(granularity, that.granularity) &&
                Objects.equals(periodicity, that.periodicity) &&
                Objects.equals(specificReservation, that.specificReservation) &&
                Objects.equals(isAllowOvernight, that.isAllowOvernight);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
