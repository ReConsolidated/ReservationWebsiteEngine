package io.github.reconsolidated.zpibackend.features.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReservationStatus {
    @JsonProperty("pending") PENDING,
    @JsonProperty("confirmed") CONFIRMED,
    @JsonProperty("cancelled") CANCELLED,
    @JsonProperty("unknown") UNKNOWN
    }
