package io.github.reconsolidated.zpiBackend.features.scheme;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreConfig {

          private Boolean simultaneous;
          private Boolean uniqueness;
          private Boolean flexibility;
          private Boolean granularity;
          private Boolean periodicity;
          private Boolean specificReservation;
}
