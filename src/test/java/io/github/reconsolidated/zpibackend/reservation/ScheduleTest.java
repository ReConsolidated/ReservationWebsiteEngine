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

    private static final int OVERNIGHT_DURATION = 30;
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
                .allowOverNight(false)
                .build();
        AuthenticationConfig authentication = AuthenticationConfig.builder()
                .confirmationRequired(false)
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
                .allowOverNight(true)
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
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 11, 30),
                LocalDateTime.of(2023, 1, 1, 12, 0),
                1, ReservationType.MORNING));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 13, 0),
                LocalDateTime.of(2023, 1, 1, 13, 30),
                1, ReservationType.OVERNIGHT));

        assertEquals(result, schedule.getAvailableScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getAvailableScheduleSlots().get(i).getType());
        }

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 15, 0),
                1));
        //creating expected result
        result.clear();
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 11, 30),
                LocalDateTime.of(2023, 1, 1, 12, 0),
                1, ReservationType.MORNING));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 15, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 15, 0),
                LocalDateTime.of(2023, 1, 1, 15, 30),
                1, ReservationType.OVERNIGHT));


        assertEquals(result, schedule.getAvailableScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getAvailableScheduleSlots().get(i).getType());
        }

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 13, 0),
                LocalDateTime.of(2023, 1, 1, 14, 0),
                1));
        //creating expected result
        result.clear();
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 11, 30),
                LocalDateTime.of(2023, 1, 1, 12, 0),
                1, ReservationType.MORNING));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 13, 0),
                LocalDateTime.of(2023, 1, 1, 14, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 14, 0),
                LocalDateTime.of(2023, 1, 1, 15, 0),
                1, ReservationType.CONTINUOUS));
        result.add(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 15, 0),
                LocalDateTime.of(2023, 1, 1, 15, 30),
                1, ReservationType.OVERNIGHT));

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
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .allowOverNight(false)
                .uniqueness(true)
                .simultaneous(true)
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
                schedule.verify(core,
                new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                        LocalDateTime.of(2023, 1, 1, 13, 0), 1)));

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0), 1));
        //fitting slot
        assertTrue(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 12, 30), 1)));
        //too big amount
        assertFalse(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 2)));
        //too early slot
        assertFalse(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 11, 30),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));
        //too late slot
        assertFalse(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 30), 1)));

    }

    @Test
    public void verifyGranularTest() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(true)
                .uniqueness(false)
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
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));

        schedule.addSlot(new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 13, 0), 1));
        //fitting slot
        assertTrue(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));
        //slot not matching whole schedule slot
        assertFalse(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 12, 30), 1)));
        //too big amount
        assertFalse(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 2)));
        //too early slot
        assertFalse(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 11, 30),
                                LocalDateTime.of(2023, 1, 1, 13, 0), 1)));
        //too late slot
        assertFalse(
                schedule.verify(core,
                        new ScheduleSlot(LocalDateTime.of(2023, 1, 1, 12, 0),
                                LocalDateTime.of(2023, 1, 1, 13, 30), 1)));

    }

    @Test
    public void processReservationTest() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .allowOverNight(false)
                .uniqueness(false)
                .simultaneous(false)
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
        schedule.processReservation(core, reservation);

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
        schedule.processReservation(core, reservation);
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
        schedule.processReservation(core, reservation);
        assertEquals(result, schedule.getAvailableScheduleSlots());
    }

    @Test
    public void testSuggest(){

        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .uniqueness(false)
                .allowOverNight(false)
                .uniqueness(false)
                .simultaneous(false)
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

        List<ScheduleSlot> suggestions = schedule.suggest(core, testSlot);

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

        suggestions = schedule.suggest(core, testSlot);

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

        suggestions = schedule.suggest(core, testSlot);

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

    @Test
    void testGetLongestAvailabilitiesEmptyList() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .uniqueness(false)
                .allowOverNight(false)
                .uniqueness(false)
                .simultaneous(false)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        Item item = Item.builder()
                .store(store)
                .amount(1)
                .build();
        Schedule schedule = new Schedule(item, new ArrayList<>());

        List<Availability> result = schedule.getLongestAvailabilities();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLongestAvailabilitiesSingleSlot() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .uniqueness(false)
                .allowOverNight(false)
                .uniqueness(false)
                .simultaneous(false)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        Item item = Item.builder()
                .store(store)
                .amount(1)
                .build();
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        Availability singleSlot = new Availability(startDateTime, endDateTime, ReservationType.CONTINUOUS);
        List<Availability> slots = List.of(singleSlot);
        Schedule schedule = new Schedule(item, slots);

        List<Availability> result = schedule.getLongestAvailabilities();

        assertEquals(1, result.size());
        assertEquals(singleSlot.getType(), result.get(0).getType());
        assertEquals(startDateTime, result.get(0).getStartDateTime());
        assertEquals(endDateTime, result.get(0).getEndDateTime());
    }

    @Test
    void testGetLongestAvailabilitiesMultipleSlots() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .uniqueness(false)
                .allowOverNight(false)
                .uniqueness(false)
                .simultaneous(false)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        Item item = Item.builder()
                .store(store)
                .amount(1)
                .build();
        LocalDateTime startDateTime1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime endDateTime1 = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime startDateTime2 = LocalDateTime.of(2023, 1, 1, 14, 0);
        LocalDateTime endDateTime2 = LocalDateTime.of(2023, 1, 1, 16, 0);
        LocalDateTime startDateTime3 = LocalDateTime.of(2023, 1, 1, 17, 0);
        LocalDateTime endDateTime3 = LocalDateTime.of(2023, 1, 1, 18, 0);
        Availability slot1 = new Availability(startDateTime1, endDateTime1, ReservationType.NONE);
        Availability slot2 = new Availability(endDateTime1, startDateTime2, ReservationType.NONE);
        Availability slot3 = new Availability(startDateTime2, endDateTime2, ReservationType.NONE);
        Availability slot4 = new Availability(startDateTime3, endDateTime3, ReservationType.NONE);

        List<Availability> slots = List.of(slot1, slot2, slot3, slot4);
        Schedule schedule = new Schedule(item, slots);

        List<Availability> result = schedule.getLongestAvailabilities();

        assertEquals(2, result.size());
        assertEquals(startDateTime1, result.get(0).getStartDateTime());
        assertEquals(endDateTime2, result.get(0).getEndDateTime());
        assertEquals(startDateTime3, result.get(1).getStartDateTime());
        assertEquals(endDateTime3, result.get(1).getEndDateTime());
    }

    @Test
    void testGetLongestAvailabilitiesOverNight() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .uniqueness(false)
                .allowOverNight(true)
                .uniqueness(false)
                .simultaneous(false)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        Item item = Item.builder()
                .store(store)
                .amount(1)
                .build();
        LocalDateTime startDateTime1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime endDateTime1 = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime startDateTime2 = LocalDateTime.of(2023, 1, 1, 14, 0);
        LocalDateTime endDateTime2 = LocalDateTime.of(2023, 1, 1, 16, 0);
        LocalDateTime startDateTime3 = LocalDateTime.of(2023, 1, 1, 17, 0);
        LocalDateTime endDateTime3 = LocalDateTime.of(2023, 1, 1, 18, 0);
        LocalDateTime startDateTime4 = LocalDateTime.of(2023, 1, 2, 17, 0);
        LocalDateTime endDateTime4 = LocalDateTime.of(2023, 1, 2, 18, 0);

        Availability slot1 = new Availability(startDateTime1, endDateTime1, ReservationType.CONTINUOUS);
        Availability slot2 = new Availability(endDateTime1, startDateTime2, ReservationType.CONTINUOUS);
        Availability slot3 = new Availability(startDateTime2, endDateTime2, ReservationType.CONTINUOUS);
        Availability slot4 = new Availability(startDateTime3, endDateTime3, ReservationType.CONTINUOUS);
        Availability slot5 = new Availability(startDateTime4, endDateTime4, ReservationType.CONTINUOUS);

        List<Availability> slots = List.of(slot1, slot2, slot3, slot4, slot5);
        Schedule schedule = new Schedule(item, slots);

        List<Availability> result = schedule.getLongestAvailabilities();

        assertEquals(startDateTime1.minusMinutes(OVERNIGHT_DURATION), result.get(0).getStartDateTime());
        assertEquals(startDateTime1, result.get(0).getEndDateTime());
        assertEquals(ReservationType.MORNING, result.get(0).getType());

        assertEquals(startDateTime1, result.get(1).getStartDateTime());
        assertEquals(endDateTime2, result.get(1).getEndDateTime());
        assertEquals(ReservationType.CONTINUOUS, result.get(1).getType());

        assertEquals(startDateTime3, result.get(2).getStartDateTime());
        assertEquals(endDateTime3, result.get(2).getEndDateTime());
        assertEquals(ReservationType.CONTINUOUS, result.get(2).getType());

        assertEquals(endDateTime3, result.get(3).getStartDateTime());
        assertEquals(endDateTime3.plusMinutes(OVERNIGHT_DURATION), result.get(3).getEndDateTime());
        assertEquals(ReservationType.OVERNIGHT, result.get(3).getType());

        assertEquals(startDateTime4.minusMinutes(OVERNIGHT_DURATION), result.get(4).getStartDateTime());
        assertEquals(startDateTime4, result.get(4).getEndDateTime());
        assertEquals(ReservationType.MORNING, result.get(4).getType());

        assertEquals(startDateTime4, result.get(5).getStartDateTime());
        assertEquals(endDateTime4, result.get(5).getEndDateTime());
        assertEquals(ReservationType.CONTINUOUS, result.get(5).getType());

        assertEquals(endDateTime4, result.get(6).getStartDateTime());
        assertEquals(endDateTime4.plusMinutes(OVERNIGHT_DURATION), result.get(6).getEndDateTime());
        assertEquals(ReservationType.OVERNIGHT, result.get(6).getType());
    }

    @Test
    public void processReservationRemovalEmptyScheduleTest() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .uniqueness(false)
                .allowOverNight(true)
                .uniqueness(false)
                .simultaneous(false)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 1, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 1, 16, 0);

        Item item = Item.builder()
                .store(store)
                .initialAmount(3)
                .amount(3)
                .build();

        Schedule schedule = new Schedule(1L, item);

        Reservation reservation = Reservation.builder()
                .item(item)
                .amount(2)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .subItemIdList(List.of(0L,2L))
                .build();

        schedule.processReservationRemoval(core, reservation);
        ScheduleSlot morningSlot = new ScheduleSlot(
                startDateTime.minusMinutes(OVERNIGHT_DURATION),
                startDateTime,
                item.getAmount(),
                ReservationType.MORNING);
        ScheduleSlot resultSlot = new ScheduleSlot(startDateTime, endDateTime, item.getAmount(), List.of(1L));
        ScheduleSlot overnightSlot = new ScheduleSlot(
                endDateTime,
                endDateTime.plusMinutes(OVERNIGHT_DURATION),
                item.getAmount(),
                ReservationType.OVERNIGHT);
        assertEquals(List.of(morningSlot, resultSlot, overnightSlot), schedule.getAvailableScheduleSlots());
    }

    @Test
    public void processReservationRemovalTest() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .uniqueness(false)
                .allowOverNight(true)
                .uniqueness(false)
                .simultaneous(false)
                .build();

        StoreConfig storeConfig = StoreConfig.builder()
                .core(core)
                .build();

        Store store = Store.builder()
                .storeConfig(storeConfig)
                .build();

        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 1, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 1, 16, 0);

        Item item = Item.builder()
                .store(store)
                .initialAmount(3)
                .amount(3)
                .build();



        Schedule schedule = new Schedule(1L, item);
        schedule.addSlot(new ScheduleSlot(startDateTime, endDateTime, item.getInitialAmount(), List.of(0L, 2L)));

        Reservation reservation = Reservation.builder()
                .item(item)
                .amount(2)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .subItemIdList(List.of(0L,2L))
                .build();

        schedule.processReservationRemoval(core, reservation);
        ScheduleSlot morningSlot = new ScheduleSlot(
                startDateTime.minusMinutes(OVERNIGHT_DURATION),
                startDateTime,
                item.getAmount(),
                ReservationType.MORNING);
        ScheduleSlot resultSlot = new ScheduleSlot(startDateTime, endDateTime, item.getAmount(), List.of());
        ScheduleSlot overnightSlot = new ScheduleSlot(
                endDateTime,
                endDateTime.plusMinutes(OVERNIGHT_DURATION),
                item.getAmount(),
                ReservationType.OVERNIGHT);
        assertEquals(List.of(morningSlot, resultSlot, overnightSlot), schedule.getAvailableScheduleSlots());
    }

}
