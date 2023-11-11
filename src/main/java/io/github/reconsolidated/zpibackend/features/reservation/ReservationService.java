package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.ItemRepository;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private ItemRepository itemRepository;

    public Reservation reserveItem(AppUser appUser, ReservationDTo request) {
        //default throws NoSuchElementException
        Item item = itemRepository.findById(request.getItemId()).orElseThrow();
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStore().getStoreConfig().getCore();

        ScheduleSlot requestSlot = new ScheduleSlot(request.getStartDateTime(), request.getEndDateTime(),
                request.getAmount());

        if(core.getFlexibility()) {
            if (!schedule.verify(core.getGranularity(), requestSlot)) {
                throw new IllegalArgumentException();
            }

            Reservation reservation = Reservation.builder()
                    .user(appUser)
                    .item(item)
                    .startDateTime(request.getStartDateTime())
                    .endDateTime(request.getEndDateTime())
                    .amount(request.getAmount())
                    .build();

            schedule.processReservation(reservation);

            return reservationRepository.save(reservation);
        } else if (core.getPeriodicity() || core.getSpecificReservation()) {


        }
        return null;
    }
}
