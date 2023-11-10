package io.github.reconsolidated.zpibackend.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.reservation.Reservation;
import io.github.reconsolidated.zpibackend.features.reservation.ReservationType;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ScheduleTest {


    @Test
    public void testAddSlotNotGranular(){

        //slots to be added to schedule
        ScheduleSlot firstSlot = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build();
        ScheduleSlot secondSlot = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 15, 0))
                .amount(1)
                .capacity(1)
                .build();
        ScheduleSlot overlappingSlot = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 30))
                .amount(1)
                .capacity(1)
                .build();
        ScheduleSlot earlierSlot = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 10, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 11, 30))
                .amount(1)
                .capacity(1)
                .build();

        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(false)
                .isAllowOvernight(false)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(firstSlot);
        ArrayList<ScheduleSlot> result = new ArrayList<>();
        result.add(firstSlot);
        assertEquals(result, schedule.getScheduleSlots());

        schedule.addSlot(secondSlot);
        result.add(secondSlot);
        assertEquals(result, schedule.getScheduleSlots());

        schedule.addSlot(overlappingSlot);
        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(2)
                .capacity(2)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 30))
                .amount(2)
                .capacity(2)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 14, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 15, 0))
                .amount(1)
                .capacity(1)
                .build());
        assertEquals(result, schedule.getScheduleSlots());

        schedule.addSlot(earlierSlot);
        result.add(0, earlierSlot);
        assertEquals(result, schedule.getScheduleSlots());
    }

    @Test
    public void testAddSlotGranular() {

        //slots to be added to schedule
        ScheduleSlot firstSlot = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build();
        ScheduleSlot secondSlot = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 15, 0))
                .amount(1)
                .capacity(1)
                .build();
        ScheduleSlot overlappingSlot = ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 30))
                .amount(1)
                .capacity(1)
                .build();

        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .granularity(true)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(firstSlot);
        ArrayList<ScheduleSlot> result = new ArrayList<>();
        result.add(firstSlot);
        assertEquals(result, schedule.getScheduleSlots());

        schedule.addSlot(secondSlot);
        result.add(secondSlot);
        assertEquals(result, schedule.getScheduleSlots());

        assertThrows(IllegalArgumentException.class, () -> schedule.addSlot(overlappingSlot));
    }

    @Test
    public void testSetSlotTypeGranular() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(false)
                .granularity(true)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build());

        assertEquals(ReservationType.SLOT, schedule.getScheduleSlots().get(0).getType());
    }

    @Test
    public void testSetSlotTypeOvernight() {
        CoreConfig core = CoreConfig.builder()
                .isAllowOvernight(true)
                .granularity(false)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build());
        //creating expected result
        ArrayList<ScheduleSlot> result = new ArrayList<>();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .type(ReservationType.OVERNIGHT)
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 2, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 2, 12, 0))
                .type(ReservationType.MORNING)
                .amount(1)
                .capacity(1)
                .build());

        assertEquals(result, schedule.getScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getScheduleSlots().get(i).getType());
        }

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 15, 0))
                .type(ReservationType.MORNING)
                .amount(1)
                .capacity(1)
                .build());
        //creating expected result
        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .type(ReservationType.CONTINUOUS)
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 15, 0))
                .type(ReservationType.OVERNIGHT)
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 2, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 2, 12, 0))
                .type(ReservationType.MORNING)
                .amount(1)
                .capacity(1)
                .build());

        assertEquals(result, schedule.getScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getScheduleSlots().get(i).getType());
        }

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 2, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 2, 14, 0))
                .type(ReservationType.MORNING)
                .amount(1)
                .capacity(1)
                .build());
        //creating expected result
        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .type(ReservationType.CONTINUOUS)
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 15, 0))
                .type(ReservationType.OVERNIGHT)
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 2, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 2, 13, 0))
                .type(ReservationType.MORNING)
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 2, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 2, 14, 0))
                .type(ReservationType.OVERNIGHT)
                .amount(1)
                .capacity(1)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 3, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 3, 13, 0))
                .type(ReservationType.MORNING)
                .amount(1)
                .capacity(1)
                .build());

        assertEquals(result, schedule.getScheduleSlots());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getType(), schedule.getScheduleSlots().get(i).getType());
        }
    }

    @Test
    public void testSetSlotTypeNotOvernight() {
        CoreConfig core = CoreConfig.builder()
                .isAllowOvernight(false)
                .granularity(true)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build());

        assertEquals(ReservationType.SLOT, schedule.getScheduleSlots().get(0).getType());
    }

    @Test
    public void verifyNotGranularTest() {
        //granularity true here is used only in setting slot type because it is the fastest
        CoreConfig core = CoreConfig.builder()
                .isAllowOvernight(false)
                .granularity(true)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        Schedule schedule = new Schedule(1L, item);
        //empty schedule
        assertFalse(
                schedule.verify(false,
                ScheduleSlot.builder()
                        .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                        .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                        .amount(1)
                        .capacity(1)
                        .build()));

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build());
        //fitting slot
        assertTrue(
                schedule.verify(false,
                ScheduleSlot.builder()
                        .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                        .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                        .amount(1)
                        .capacity(1)
                        .build()));
        //too big amount and capacity
        assertFalse(
                schedule.verify(false,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                                .amount(2)
                                .capacity(2)
                                .build()));
        //too early slot
        assertFalse(
                schedule.verify(false,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 11, 30))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                                .amount(1)
                                .capacity(1)
                                .build()));
        //too late slot
        assertFalse(
                schedule.verify(false,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                                .amount(1)
                                .capacity(1)
                                .build()));

    }

    @Test
    public void verifyGranularTest() {
        CoreConfig core = CoreConfig.builder()
                .isAllowOvernight(false)
                .granularity(true)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        Schedule schedule = new Schedule(1L, item);
        //empty schedule
        assertFalse(
                schedule.verify(true,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                                .amount(1)
                                .capacity(1)
                                .build()));

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build());
        //fitting slot
        assertTrue(
                schedule.verify(true,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                                .amount(1)
                                .capacity(1)
                                .build()));
        //slot not matching whole schedule slot
        assertFalse(
                schedule.verify(true,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 12, 30))
                                .amount(1)
                                .capacity(1)
                                .build()));
        //too big amount and capacity
        assertFalse(
                schedule.verify(true,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                                .amount(2)
                                .capacity(2)
                                .build()));
        //too early slot
        assertFalse(
                schedule.verify(true,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 11, 30))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                                .amount(1)
                                .capacity(1)
                                .build()));
        //too late slot
        assertFalse(
                schedule.verify(true,
                        ScheduleSlot.builder()
                                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                                .amount(1)
                                .capacity(1)
                                .build()));

    }

    @Test
    public void processReservationTest() {
        CoreConfig core = CoreConfig.builder()
                .isAllowOvernight(false)
                .granularity(true)
                .build();

        StoreConfig store = StoreConfig.builder()
                .core(core)
                .build();

        Item item = Item.builder()
                .storeConfig(store)
                .build();

        AppUser appUser = new AppUser();

        Schedule schedule = new Schedule(1L, item);

        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .capacity(1)
                .build());
        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .amount(2)
                .capacity(3)
                .build());
        schedule.addSlot(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .amount(2)
                .capacity(3)
                .build());

        ArrayList<ScheduleSlot> result = new ArrayList<>();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .amount(2)
                .capacity(3)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .amount(2)
                .capacity(3)
                .build());

        Reservation reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .startDateTime(LocalDateTime.of(2023, 1, 1, 12, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .amount(1)
                .places(1)
                .build();
        schedule.processReservation(reservation);

        assertEquals(result, schedule.getScheduleSlots());

        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .amount(1)
                .capacity(3)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .amount(1)
                .capacity(3)
                .build());

        reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .amount(1)
                .places(0)
                .build();
        schedule.processReservation(reservation);
        assertEquals(result, schedule.getScheduleSlots());

        result.clear();
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 0))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .amount(1)
                .capacity(3)
                .build());
        result.add(ScheduleSlot.builder()
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 30))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 13, 45))
                .amount(1)
                .capacity(3)
                .build());

        reservation = Reservation.builder()
                .user(appUser)
                .item(item)
                .startDateTime(LocalDateTime.of(2023, 1, 1, 13, 45))
                .endDateTime(LocalDateTime.of(2023, 1, 1, 14, 0))
                .amount(1)
                .places(0)
                .build();
        schedule.processReservation(reservation);
        assertEquals(result, schedule.getScheduleSlots());
    }
}
