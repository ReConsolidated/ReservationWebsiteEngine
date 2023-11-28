package io.github.reconsolidated.zpibackend.domain.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReservationStatus {
    @JsonProperty("pending") PENDING,
    @JsonProperty("confirmed") CONFIRMED,
    @JsonProperty("cancelled") CANCELLED,
    @JsonProperty("unknown") UNKNOWN
    }
