package io.github.reconsolidated.zpibackend.reservation;

import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.reservation.ReservationType;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import io.github.reconsolidated.zpibackend.features.reservation.ScheduleSlot;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class ScheduleTest {


    @Test
    public void testAddSlotFlexible(){

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
    public void testAddSlotNotFlexible() {

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
                .flexibility(true)
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

        assertEquals(ReservationType.SLOT, schedule.getScheduleSlots().get(0).getType());
    }

    @Test
    public void testSetSlotTypeNotOvernight() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(true)
                .isAllowOvernight(false)
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

        assertEquals(ReservationType.CONTINUOUS, schedule.getScheduleSlots().get(0).getType());
    }

    @Test
    public void testSetSlotTypeNone() {
        CoreConfig core = CoreConfig.builder()
                .flexibility(false)
                .isAllowOvernight(false)
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

        assertEquals(ReservationType.NONE, schedule.getScheduleSlots().get(0).getType());
    }
}
