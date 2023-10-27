package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    private ReservationRepository reservationRepository;

    public Reservation reserveItem(AppUser appUser, CheckAvailabilityRequest request) {

        Item item = new Item();

        if(!verifyTime(item.getSchedule(), request.getStartDate(), request.getEndDate(), item.getStoreConfig().getCore())) {
            throw new IllegalArgumentException();
        }

        ScheduleSlot requestSlot = ScheduleSlot.builder()
                .startDateTime(request.getStartDate())
                .endDateTime(request.getEndDate())
                .amount(request.getAmount())
                .capacity(request.getPlaces())
                //TODO ZPI-90 implement types
                //.type()
                .build();

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .scheduleSlot(requestSlot)
                .build();



        return reservationRepository.save(reservation);
    }

    public Boolean verifyTime(Schedule schedule, LocalDateTime startDateTime, LocalDateTime endDateTime, CoreConfig config) {

        if(config.getFlexibility()) {
            if(config.getGranularity()) {
                //return scheduleSlot.overlaps(schedule.getScheduleSlots());
            }
            else {
                //boolean t = schedule.getScheduleSlots().stream().anyMatch(scheduleSlot::isIncluded);
            }

        }
        else {



        }

        return false;
    }
}
