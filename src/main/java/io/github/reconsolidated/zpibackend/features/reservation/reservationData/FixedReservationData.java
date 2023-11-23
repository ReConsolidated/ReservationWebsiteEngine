package io.github.reconsolidated.zpibackend.features.reservation.reservationData;

import io.github.reconsolidated.zpibackend.features.item.dtos.SubItemDto;

import java.util.List;

public record FixedReservationData(List<SubItemDto> subItemList, Integer amount) implements ReservationData{
}
