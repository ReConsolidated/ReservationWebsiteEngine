package io.github.reconsolidated.zpibackend.items;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.item.ItemMapper;
import io.github.reconsolidated.zpibackend.domain.item.ItemRepository;
import io.github.reconsolidated.zpibackend.domain.item.ItemService;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemAttributesDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ScheduleDto;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.store.StoreService;
import io.github.reconsolidated.zpibackend.domain.storeConfig.CoreConfig;
import io.github.reconsolidated.zpibackend.domain.storeConfig.Owner;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    private AppUser currentUser;
    private Store store;
    private Item item;
    private ItemDto itemDto;
    private Long itemId;
    private String storeName;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        currentUser = new AppUser();
        currentUser.setId(1L);

        StoreConfig storeConfig = mock(StoreConfig.class);
        CoreConfig core = mock(CoreConfig.class);
        Owner owner = mock(Owner.class);
        when(storeConfig.getOwner()).thenReturn(owner);
        when(storeConfig.getCore()).thenReturn(core);
        when(core.getFlexibility()).thenReturn(true);
        when(owner.getAppUserId()).thenReturn(currentUser.getId());
        store = Store.builder()
                .id(5L)
                .storeConfig(storeConfig)
                .build();
        item = new Item();
        itemDto = new ItemDto();
        itemDto.setAttributes(ItemAttributesDto.builder().title("TestTitle").build());
        itemDto.setCustomAttributeList(new ArrayList<>());
        itemDto.setSchedule(new ScheduleDto(LocalDateTime.now(), LocalDateTime.now()));
        itemDto.setSubItems(new ArrayList<>());
        itemId = 1L;
        storeName = "TestStore";
    }

    @Test
    void getItem_ShouldReturnItem_WhenItemExists() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItem(itemId);

        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    void getItem_ShouldThrowException_WhenItemDoesNotExist() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.getItem(itemId));
    }

    @Test
    void getItemFromStore_ShouldReturnItem_WhenExistsInStore() {
        when(itemRepository.findByStoreStoreNameAndItemId(storeName, itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItemFromStore(itemId, storeName);

        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    void getItemFromStore_ShouldThrowException_WhenItemDoesNotExistsInStore() {
        when(itemRepository.findByStoreStoreNameAndItemId(storeName, itemId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.getItemFromStore(itemId, storeName));
    }

    @Test
    void createItem_ShouldCreateItem_WhenUserIsOwner() {
        when(storeService.getStore(storeName)).thenReturn(store);
        when(store.getStoreConfig().getOwner().getAppUserId()).thenReturn(currentUser.getId());
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.createItem(currentUser, storeName, itemDto);

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowException_WhenUserIsNotOwner() {
        when(storeService.getStore(storeName)).thenReturn(store);
        when(store.getStoreConfig().getOwner().getAppUserId()).thenReturn(currentUser.getId() + 1);

        assertThrows(RuntimeException.class, () -> itemService.createItem(currentUser, storeName, itemDto));
    }



}

