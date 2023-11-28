package io.github.reconsolidated.zpibackend.reservation;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationType;
import io.github.reconsolidated.zpibackend.domain.reservation.Schedule;
import io.github.reconsolidated.zpibackend.domain.reservation.ScheduleSlot;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.storeConfig.AuthenticationConfig;
import io.github.reconsolidated.zpibackend.domain.storeConfig.CoreConfig;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ScheduleTest {

    @Test
    public void testAddSlot() {

        //slots to be added to schedule
        ScheduleSlot firstSlot = new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1);
        ScheduleSlot secondSlot = new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 14, 0),
                1);
        ScheduleSlot overlappingSlot = new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 30),
                LocalDateTime.of(2023, 1, 1, 14, 30),
                1);

        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(true)
                .build();
        AuthenticationConfig authentication = AuthenticationConfig.builder()
                .confirmationRequire(false)
                .build();
        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .authConfig(authentication)
                .build();
        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();
        Item item = Item.builder()
                .store(store)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(firstSlot);
        ArrayList<ScheduleSlot> result = new ArrayList<>();
        result.add(firstSlot);
        assertEquals(result, schedule.getAvailableScheduleSlots());

        schedule.addSlot(secondSlot);
        result.add(secondSlot);
        assertEquals(result, schedule.getAvailableScheduleSlots());

        assertThrows(IllegalArgumentException.class, () -> schedule.addSlot(overlappingSlot));
    }

    @Test
    public void testSetSlotTypeGranular() {
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
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1));

        assertEquals(ReservationType.SLOT, schedule.getAvailableScheduleSlots().get(0).getType());
    }

    @Test
    public void testSetSlotTypeOvernight() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .isAllowOvernight(true)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        Item item = Item.builder()
                .store(store)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1));
        //creating expected result
        ArrayList<ScheduleSlot> result = new ArrayList<>();
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1, ReservationType.OVERNIGHT));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 2, 12, 0),
                LocalDateTime.of(2023, 1, 2, 12, 0),
                1, ReservationType.MORNING));

        assertEquals(result, schedule.getAvailableScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getAvailableScheduleSlots().get(i).getType());
        }

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 15, 0),
                1));
        //creating expected result
        result.clear();
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 15, 0),
                1, ReservationType.OVERNIGHT));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 2, 12, 0),
                LocalDateTime.of(2023, 1, 2, 12, 0),
                1, ReservationType.MORNING));

        assertEquals(result, schedule.getAvailableScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getAvailableScheduleSlots().get(i).getType());
        }

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 2, 13, 0),
                LocalDateTime.of(2023, 1, 2, 14, 0),
                1));
        //creating expected result
        result.clear();
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 15, 0),
                1, ReservationType.OVERNIGHT));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 2, 13, 0),
                LocalDateTime.of(2023, 1, 2, 13, 0),
                1, ReservationType.MORNING));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 2, 13, 0),
                LocalDateTime.of(2023, 1, 2, 14, 0),
                1, ReservationType.OVERNIGHT));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 3, 13, 0),
                LocalDateTime.of(2023, 1, 3, 13, 0),
                1, ReservationType.MORNING));

        assertEquals(result, schedule.getAvailableScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getAvailableScheduleSlots().get(i).getType());
        }
    }

    @Test
    public void testSetSlotTypeNotOvernight() {
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
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0), 1));

        assertEquals(ReservationType.SLOT, schedule.getAvailableScheduleSlots().get(0).getType());
    }

    @Test
    public void verifyNotGranularTest() {
        //granularity true here is used only in setting slot type because it is the fastest
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
                .amount(1)
                .store(store)
                .build();

        Schedule schedule = new Schedule(1L, item);
        //empty schedule
        assertFalse(
                schedule.verify(false,
                new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                        LocalDateTime.of(2023, 1, 1, 13, 0), 1)));

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0), 1));
        //fitting slot
        assertTrue(
                schedule.verify(false,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 12, 30), 1)));
        //too big amount
        assertFalse(
                schedule.verify(false,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 2)));
        //too early slot
        assertFalse(
                schedule.verify(false,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 11, 30),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));
        //too late slot
        assertFalse(
                schedule.verify(false,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 30), 1)));

    }

    @Test
    public void verifyGranularTest() {
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
                .build();

        Schedule schedule = new Schedule(1L, item);
        //empty schedule
        assertFalse(
                schedule.verify(true,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0), 1));
        //fitting slot
        assertTrue(
                schedule.verify(true,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));
        //slot not matching whole schedule slot
        assertFalse(
                schedule.verify(true,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 12, 30), 1)));
        //too big amount
        assertFalse(
                schedule.verify(true,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 2)));
        //too early slot
        assertFalse(
                schedule.verify(true,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 11, 30),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));
        //too late slot
        assertFalse(
                schedule.verify(true,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 30), 1)));

    }

    @Test
    public void processReservationTest() {
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
                .amount(2)
                .build();

        AppUser appUser = new AppUser();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 14, 0), 2));

        ArrayList<ScheduleSlot> result = new ArrayList<>();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .currAmount(1)
                .itemsAvailability(Arrays.asList(false, true))
                .build());
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 13, 0),
                LocalDateTime.of(2023, 1, 1, 14, 0), 2));

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .subItemIdList(new ArrayList<>())
                .build();
        schedule.processReservation(reservation);

        assertEquals(result, schedule.getAvailableScheduleSlots());

        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .currAmount(1)
                .itemsAvailability(Arrays.asList(false, true))
                .build());

        reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .amount(2)
                .subItemIdList(new ArrayList<>())
                .build();
        schedule.processReservation(reservation);
        assertEquals(result, schedule.getAvailableScheduleSlots());

        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 45))
                .currAmount(1)
                .itemsAvailability(Arrays.asList(false, true))
                .build());

        reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 45))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .subItemIdList(new ArrayList<>())
                .build();
        schedule.processReservation(reservation);
        assertEquals(result, schedule.getAvailableScheduleSlots());

        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 45))
                .currAmount(1)
                .itemsAvailability(Arrays.asList(false, true))
                .type(ReservationType.SLOT)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 45))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .currAmount(1)
                .itemsAvailability(Arrays.asList(true, false))
                .type(ReservationType.NONE)
                .build());

        schedule.processReservationRemoval(reservation);
        assertEquals(result, schedule.getAvailableScheduleSlots());

    }

    @Test
    public void testSuggest(){

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
                .amount(2)
                .build();

        Schedule schedule = new Schedule(1L, item);
        //empty schedule -> no suggestions
        ScheduleSlot testSlot = new ScheduleSlot(
                LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                2);

        List<ScheduleSlot> suggestions = schedule.suggest(testSlot);

        ArrayList<ScheduleSlot> expected = new ArrayList<>();

        assertEquals(expected, suggestions);
        item.setAmount(4);
        //all types of suggestions
        ScheduleSlot dayBeforeSlot = new ScheduleSlot(
                LocalDateTime.of(2022, 12, 31, 12, 0),
                LocalDateTime.of(2022, 12, 31, 13, 0),
                4);
        ScheduleSlot dayAfterSlotSchedule = new ScheduleSlot(
                LocalDateTime.of(2023, 1, 2, 11, 0),
                LocalDateTime.of(2023, 1, 2, 13, 0),
                4);
        ScheduleSlot dayAfterSlot = new ScheduleSlot(
                LocalDateTime.of(2023, 1, 2, 12, 0),
                LocalDateTime.of(2023, 1, 2, 13, 0),
                4);
        ScheduleSlot weekAfterSlotSchedule = new ScheduleSlot(
                LocalDateTime.of(2023, 1, 8, 12, 0),
                LocalDateTime.of(2023, 1, 8, 14, 0),
                4);
        ScheduleSlot weekAfterSlot = new ScheduleSlot(
                LocalDateTime.of(2023, 1, 8, 12, 0),
                LocalDateTime.of(2023, 1, 8, 13, 0),
                4);
        ScheduleSlot daySlot1 = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 11, 30))
                .currAmount(3)
                .itemsAvailability(List.of(true, true, true, false))
                .build();
        ScheduleSlot daySlot2 = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 11, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                .currAmount(2)
                .itemsAvailability(List.of(false, true, true, false))
                .build();
        ScheduleSlot daySlotMerged = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                .currAmount(2)
                .itemsAvailability(List.of(false, true, true, false))
                .build();
        schedule.addSlot(dayBeforeSlot);
        schedule.addSlot(daySlot1);
        schedule.addSlot(daySlot2);
        schedule.addSlot(dayAfterSlotSchedule);
        schedule.addSlot(weekAfterSlotSchedule);

        expected.clear();
        expected.add(daySlotMerged);
        expected.add(dayBeforeSlot);
        expected.add(dayAfterSlot);
        expected.add(weekAfterSlot);

        suggestions = schedule.suggest(testSlot);

        assertEquals(expected, suggestions);

        //splitting slots with enough amount because of different sub item indexes
        schedule = new Schedule(1L, item);

        ScheduleSlot daySlot3 = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 11, 30))
                .currAmount(3)
                .itemsAvailability(List.of(true, true, true, false))
                .build();
        ScheduleSlot daySlot4 = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 11, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                .currAmount(2)
                .itemsAvailability(List.of(true, false, false, true))
                .build();
        ScheduleSlot daySlot5 = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 15, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 16, 30))
                .currAmount(1)
                .itemsAvailability(List.of(false, false, false, true))
                .build();

        schedule.addSlot(daySlot3);
        schedule.addSlot(daySlot4);
        schedule.addSlot(daySlot5);

        expected.clear();
        expected.add(daySlot3);
        expected.add(daySlot4);

        suggestions = schedule.suggest(testSlot);

        assertEquals(expected, suggestions);
    }

    @Test
    public void getAvailabilityForSubItemsTest() {

        Schedule schedule = new Schedule();

        List<ScheduleSlot> slots = Arrays.asList(
          ScheduleSlot.builder()
                  .startDateTime(LocalDateTime.of(2023,1,1,10,0))
                  .endDateTime(LocalDateTime.of(2023,1,1,11,0))
                  .itemsAvailability(Arrays.asList(true, true, true, true))
                  .type(ReservationType.NONE)
                  .build(),
          ScheduleSlot.builder()
                  .startDateTime(LocalDateTime.of(2023,1,1,11,0))
                  .endDateTime(LocalDateTime.of(2023,1,1,12,0))
                  .itemsAvailability(Arrays.asList(true, true, false, true))
                  .type(ReservationType.NONE)
                  .build(),
          ScheduleSlot.builder()
                  .startDateTime(LocalDateTime.of(2023,1,1,13,0))
                  .endDateTime(LocalDateTime.of(2023,1,1,14,0))
                  .itemsAvailability(Arrays.asList(true, false, true, true))
                  .type(ReservationType.NONE)
                  .build(),
          ScheduleSlot.builder()
                  .startDateTime(LocalDateTime.of(2023,1,1,14,0))
                  .endDateTime(LocalDateTime.of(2023,1,1,15,0))
                  .itemsAvailability(Arrays.asList(false, false, true, true))
                  .type(ReservationType.NONE)
                  .build(),
          ScheduleSlot.builder()
                  .startDateTime(LocalDateTime.of(2023,1,1,16,0))
                  .endDateTime(LocalDateTime.of(2023,1,1,17,0))
                  .itemsAvailability(Arrays.asList(true, false, true, false))
                  .type(ReservationType.NONE)
                  .build(),
          ScheduleSlot.builder()
                  .startDateTime(LocalDateTime.of(2023,1,1,17,0))
                  .endDateTime(LocalDateTime.of(2023,1,1,18,0))
                  .itemsAvailability(Arrays.asList(true, false, false, true))
                  .type(ReservationType.NONE)
                  .build()
        );
        schedule.setAvailableScheduleSlots(slots);

        List<Integer> subItemsId = Arrays.asList(0,3);

        List<Availability> expected = Arrays.asList(
                new Availability(LocalDateTime.of(2023,1,1,10,0),
                        LocalDateTime.of(2023,1,1,11,0),
                    ReservationType.NONE),
                new Availability(LocalDateTime.of(2023,1,1,11,0),
                        LocalDateTime.of(2023,1,1,12,0),
                        ReservationType.NONE),
                new Availability(LocalDateTime.of(2023,1,1,13,0),
                        LocalDateTime.of(2023,1,1,14,0),
                        ReservationType.NONE),
                new Availability(LocalDateTime.of(2023,1,1,17,0),
                        LocalDateTime.of(2023,1,1,18,0),
                        ReservationType.NONE)
        );

        assertEquals(expected, schedule.getAvailabilitiesForSubItems(subItemsId));
    }

}
