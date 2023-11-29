package io.github.reconsolidated.zpibackend.domain.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReservationStatus {
    @JsonProperty("active") ACTIVE,
    @JsonProperty("past") PAST,
    @JsonProperty("cancelled_by_user") CANCELLED_BY_USER,
    @JsonProperty("cancelled_by_admin") CANCELLED_BY_ADMIN
}
