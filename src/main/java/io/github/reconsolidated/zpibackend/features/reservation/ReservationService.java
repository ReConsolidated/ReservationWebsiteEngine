package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.ItemRepository;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private ReservationRepository reservationRepository;
    private ItemRepository itemRepository;

    public Reservation reserveItem(AppUser appUser, CheckAvailabilityRequest request) {
        //default throws NoSuchElementException
        Item item = itemRepository.findById(request.getItemId()).orElseThrow();
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStoreConfig().getCore();

        if(!schedule.verify(core.getFlexibility(), request.getStartDate(), request.getEndDate(), request.getAmount(),
                request.getPlaces())) {
            throw new IllegalArgumentException();
        }

        ScheduleSlot requestSlot = ScheduleSlot.builder()
                .startDateTime(request.getStartDate())
                .endDateTime(request.getEndDate())
                .amount(request.getAmount())
                .capacity(request.getPlaces())
                .type(ReservationType.NONE)
                .build();

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .scheduleSlot(requestSlot)
                .amount(request.getAmount())
                .places(request.getPlaces())
                .build();

        schedule.processReservation(reservation);

        return reservationRepository.save(reservation);
    }
}
