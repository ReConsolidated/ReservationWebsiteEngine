package io.github.reconsolidated.zpibackend.reservation;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationType;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.storeConfig.CoreConfig;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ScheduleSlotTest {

    @Test
    public void scheduleSlotFromLongsList() {

        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(true)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        Item item = Item.builder()
                .store(store)
                .amount(5)
                .build();

        AppUser appUser = new AppUser();

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(3)
                .subItemIdList(Arrays.asList(0L,1L,4L))
                .build();

        ScheduleSlot expected = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .currAmount(2)
                .itemsAvailability(Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE))
                .type(ReservationType.NONE)
                .build();

        assertEquals(expected, reservation.getScheduleSlot());
    }
}
