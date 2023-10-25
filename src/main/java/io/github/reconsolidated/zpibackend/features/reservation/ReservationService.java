package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private ReservationRepository reservationRepository;

    public Reservation reserveItem(AppUser appUser, Item item, ScheduleSlot scheduleSlot){

        StoreConfig storeConfig = item.getStoreConfig();
        CoreConfig coreConfig = storeConfig.getCore();

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)

                .build();



        return reservation;
    }

    public Boolean verifyTime(Schedule schedule, ScheduleSlot scheduleSlot, CoreConfig config) {



        return false;
    }
}
