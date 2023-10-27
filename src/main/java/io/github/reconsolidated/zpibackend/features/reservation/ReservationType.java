package io.github.reconsolidated.zpibackend.features.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReservationType {

    NONE,
    @JsonProperty("morning") MORNING,
    @JsonProperty("slot") SLOT,
    @JsonProperty("continuous") CONTINUOUS,
    @JsonProperty("continuous") OVERNIGHT
}
