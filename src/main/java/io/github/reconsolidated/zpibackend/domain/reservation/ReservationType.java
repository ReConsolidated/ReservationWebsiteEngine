package io.github.reconsolidated.zpibackend.domain.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReservationType {

    NONE,
    @JsonProperty("morning") MORNING,
    @JsonProperty("slot") SLOT,
    @JsonProperty("continuous") CONTINUOUS,
    @JsonProperty("overnight") OVERNIGHT
}
