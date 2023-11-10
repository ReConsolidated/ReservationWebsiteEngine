package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.ItemRepository;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private ItemRepository itemRepository;

    public Reservation reserveItem(AppUser appUser, CheckAvailabilityRequest request) {
        //default throws NoSuchElementException
        Item item = itemRepository.findById(request.getItemId()).orElseThrow();
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStoreConfig().getCore();

        ScheduleSlot requestSlot = ScheduleSlot.builder()
                .startDateTime(request.getStartDate())
                .endDateTime(request.getEndDate())
                .amount(request.getAmount())
                .capacity(request.getPlaces())
                .type(ReservationType.NONE)
                .build();

        if(core.getFlexibility()) {
            //reservation with schedule
            //TODO ask about specific items with schedule
            //TODO specific + periodicity
            if (!schedule.verify(core.getGranularity(), requestSlot)) {
                throw new IllegalArgumentException();
            }

            Reservation reservation = Reservation.builder()
                    .user(appUser)
                    .item(item)
                    .startDateTime(request.getStartDate())
                    .endDateTime(request.getEndDate())
                    .amount(request.getAmount())
                    .places(request.getPlaces())
                    .build();

            schedule.processReservation(reservation);

            return reservationRepository.save(reservation);
        } else if (core.getPeriodicity() || core.getSpecificReservation()) {

        }
        return null;
    }
}
