package io.github.reconsolidated.zpibackend.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.ItemService;
import io.github.reconsolidated.zpibackend.features.item.SubItem;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.features.reservation.Reservation;
import io.github.reconsolidated.zpibackend.features.reservation.ReservationService;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import io.github.reconsolidated.zpibackend.features.reservation.dtos.ReservationDto;
import io.github.reconsolidated.zpibackend.features.store.Store;
import io.github.reconsolidated.zpibackend.features.store.StoreService;
import io.github.reconsolidated.zpibackend.features.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.*;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationServiceTests {
    private static final String STORE_NAME = "ReservationServiceTests";
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private StoreConfigService storeConfigService;

    @Test
    @Transactional
    public void reserveFlexibleTest() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .isAllowOvernight(false)
                .periodicity(false)
                .simultaneous(false)
                .specificReservation(false)
                .uniqueness(false)
                .build();
        Owner owner = Owner.builder()
                .storeName(STORE_NAME)
                .email("mail@mail")
                .color("blue")
                .build();
        StoreConfig storeConfig = StoreConfig.builder()
                .name(STORE_NAME)
                .owner(owner)
                .authConfig(new AuthenticationConfig())
                .core(core)
                .mainPage(MainPageConfig.builder().build())
                .detailsPage(DetailsPageConfig.builder().build())
                .build();
        AppUser appUser = appUserService.getOrCreateUser("1", "test@test", "FirstName", "LastName");

        storeConfig = storeConfigService.createStoreConfig(appUser, new StoreConfigDto(storeConfig));
        storeService.createStore(appUser, new CreateStoreDto(storeConfig.getStoreConfigId(), storeConfig.getName()));
        Store store = storeService.getStore(STORE_NAME);
        Item item = Item.builder()
                .active(true)
                .store(store)
                .amount(2)
                .subItems(new ArrayList<>())
                .customAttributeList(new ArrayList<>())
                .initialAmount(2)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 14, 0), 2));

        item.setSchedule(schedule);
        item.setInitialSchedule(schedule);

        item = itemService.createItem(appUser, STORE_NAME, new ItemDto(item));

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 14, 0);

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .subItemIdList(Arrays.asList(0L,1L))
                .startDateTime(start)
                .endDateTime(end)
                .amount(2)
                .message("message")
                .confirmed(true)
                .build();

        ReservationDto reservationDto = new ReservationDto(reservation, null);

        ReservationDto result = reservationService.reserveItem(appUser, reservationDto);

        assertEquals(reservationDto.getStartDateTime(), result.getStartDateTime());
        assertEquals(reservationDto.getEndDateTime(), result.getEndDateTime());
        assertEquals(reservationDto.getSubItemIds(), result.getSubItemIds());
        assertEquals(reservationDto.getAmount(), result.getAmount());
        assertEquals(reservationDto.getStatus(), result.getStatus());
    }

    @Test
    @Transactional
    public void reserveFixedTest() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(false)
                .granularity(false)
                .isAllowOvernight(false)
                .periodicity(true)
                .simultaneous(false)
                .specificReservation(false)
                .uniqueness(false)
                .build();
        Owner owner = Owner.builder()
                .storeName(STORE_NAME)
                .email("mail@mail")
                .color("blue")
                .build();
        StoreConfig storeConfig = StoreConfig.builder()
                .name(STORE_NAME)
                .owner(owner)
                .authConfig(new AuthenticationConfig())
                .core(core)
                .mainPage(MainPageConfig.builder().build())
                .detailsPage(DetailsPageConfig.builder().build())
                .build();
        AppUser appUser = appUserService.getOrCreateUser("1", "test@test", "FirstName", "LastName");

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 14, 0);
        ScheduleSlot subItemSlot = new ScheduleSlot(start, end, 1);

        storeConfig = storeConfigService.createStoreConfig(appUser, new StoreConfigDto(storeConfig));
        storeService.createStore(appUser, new CreateStoreDto(storeConfig.getStoreConfigId(), storeConfig.getName()));
        Store store = storeService.getStore(STORE_NAME);
        SubItem subItem0 = SubItem.builder()
                .amount(1)
                .slot(subItemSlot)
                .build();
        SubItem subItem1 = SubItem.builder()
                .amount(1)
                .slot(subItemSlot)
                .build();
        SubItem subItem2 = SubItem.builder()
                .amount(1)
                .slot(subItemSlot)
                .build();

        Item item = Item.builder()
                .active(true)
                .store(store)
                .amount(3)
                .subItems(Arrays.asList(subItem0, subItem1, subItem2))
                .customAttributeList(new ArrayList<>())
                .initialAmount(3)
                .build();
        Schedule schedule = new Schedule(item, new ArrayList<>());
        item.setSchedule(schedule);
        item.setInitialSchedule(schedule);

        item = itemService.createItem(appUser, STORE_NAME, new ItemDto(item));

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .subItemIdList(Arrays.asList(subItem1.getSubItemId(), subItem2.getSubItemId()))
                .startDateTime(start)
                .endDateTime(end)
                .amount(1)
                .message("message")
                .confirmed(true)
                .build();

        ReservationDto reservationDto = new ReservationDto(reservation, null);

        ReservationDto result = reservationService.reserveItem(appUser, reservationDto);

        assertEquals(reservationDto.getStartDateTime(), result.getStartDateTime());
        assertEquals(reservationDto.getEndDateTime(), result.getEndDateTime());
        assertEquals(reservationDto.getSubItemIds(), result.getSubItemIds());
        assertEquals(reservationDto.getAmount(), result.getAmount());
        assertEquals(reservationDto.getStatus(), result.getStatus());
    }
}

