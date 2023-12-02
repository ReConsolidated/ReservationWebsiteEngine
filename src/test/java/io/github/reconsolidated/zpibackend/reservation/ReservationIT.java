package io.github.reconsolidated.zpibackend.reservation;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.item.ItemService;
import io.github.reconsolidated.zpibackend.domain.item.SubItem;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.domain.reservation.*;
import io.github.reconsolidated.zpibackend.domain.storeConfig.*;
import io.github.reconsolidated.zpibackend.domain.reservation.dtos.ReservationDto;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.store.StoreService;
import io.github.reconsolidated.zpibackend.domain.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.dtos.StoreConfigDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationIT {
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
                .allowOverNight(false)
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
                .owner(owner)
                .authConfig(new AuthenticationConfig())
                .core(core)
                .mainPage(MainPageConfig.builder().build())
                .detailsPage(DetailsPageConfig.builder().build())
                .customAttributesSpec(new ArrayList<>())
                .build();
        AppUser appUser = appUserService.getOrCreateUser("1", "test@test", "FirstName", "LastName");

        storeConfig = storeConfigService.createStoreConfig(appUser, new StoreConfigDto(storeConfig));
        storeService.createStore(
                appUser,
                new CreateStoreDto(
                        storeConfig.getStoreConfigId(),
                        storeConfig.getOwner().getStoreName().replaceAll("[ /]", "_")));
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
                LocalDateTime.of(2023, 1, 1, 14, 0), item.getInitialAmount()));

        item.setSchedule(schedule);
        item.setInitialSchedule(schedule);

        item = itemService.createItem(appUser, STORE_NAME, new ItemDto(item, 0.0, 0));

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

        ReservationDto reservationDto = new ReservationDto(reservation);

        ReservationDto result = reservationService.reserveItem(appUser, STORE_NAME, reservationDto);

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
                .allowOverNight(false)
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
                .owner(owner)
                .authConfig(new AuthenticationConfig())
                .core(core)
                .mainPage(MainPageConfig.builder().build())
                .detailsPage(DetailsPageConfig.builder().build())
                .customAttributesSpec(new ArrayList<>())
                .build();
        AppUser appUser = appUserService.getOrCreateUser("1", "test@test", "FirstName", "LastName");

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 14, 0);
        ScheduleSlot subItemSlot = new ScheduleSlot(start, end, 1);

        storeConfig = storeConfigService.createStoreConfig(appUser, new StoreConfigDto(storeConfig));
        storeService.createStore(
                appUser,
                new CreateStoreDto(
                        storeConfig.getStoreConfigId(),
                        STORE_NAME.replaceAll("[ /]", "_")));
        Store store = storeService.getStore(STORE_NAME);
        SubItem subItem0 = SubItem.builder()
                .amount(1)
                .startDateTime(subItemSlot.getStartDateTime())
                .endDateTime(subItemSlot.getEndDateTime())
                .amount(subItemSlot.getCurrAmount())
                .build();
        SubItem subItem1 = SubItem.builder()
                .amount(1)
                .startDateTime(subItemSlot.getStartDateTime())
                .endDateTime(subItemSlot.getEndDateTime())
                .amount(subItemSlot.getCurrAmount())
                .build();
        SubItem subItem2 = SubItem.builder()
                .amount(1)
                .startDateTime(subItemSlot.getStartDateTime())
                .endDateTime(subItemSlot.getEndDateTime())
                .amount(subItemSlot.getCurrAmount())
                .build();

        Item item = Item.builder()
                .active(true)
                .store(store)
                .amount(3)
                .subItems(Arrays.asList(subItem0, subItem1, subItem2))
                .customAttributeList(new ArrayList<>())
                .initialAmount(3)
                .build();
        Schedule schedule = new Schedule(item, List.of(
                new Availability(LocalDateTime.of(2023,1,1, 10,0),
                        LocalDateTime.of(2023,1,1, 15,0),
                        ReservationType.NONE)));
        item.setSchedule(schedule);
        item.setInitialSchedule(schedule);

        item = itemService.createItem(appUser, STORE_NAME, new ItemDto(item, 0.0, 0));

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .subItemIdList(Arrays.asList(item.getSubItems().get(1).getSubItemId(), item.getSubItems().get(2).getSubItemId()))
                .startDateTime(start)
                .endDateTime(end)
                .amount(1)
                .message("message")
                .confirmed(true)
                .build();

        ReservationDto reservationDto = new ReservationDto(reservation);

        ReservationDto result = reservationService.reserveItem(appUser, STORE_NAME, reservationDto);

        assertEquals(reservationDto.getStartDateTime(), result.getStartDateTime());
        assertEquals(reservationDto.getEndDateTime(), result.getEndDateTime());
        assertEquals(reservationDto.getSubItemIds(), result.getSubItemIds());
        assertEquals(reservationDto.getAmount(), result.getAmount());
        assertEquals(reservationDto.getStatus(), result.getStatus());
    }
}

