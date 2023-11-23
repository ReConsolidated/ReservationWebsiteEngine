package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemListDto;
import io.github.reconsolidated.zpibackend.features.parameter.ParameterRepository;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import io.github.reconsolidated.zpibackend.features.store.Store;
import io.github.reconsolidated.zpibackend.features.store.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ParameterRepository parameterRepository;
    private final StoreService storeService;

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow();
    }

    public ItemListDto getItems(AppUser currentUser, String storeName) {
        Store store = storeService.getStore(storeName);
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        return new ItemListDto(itemRepository
                .findAllByStore_Id(store.getId())
                .stream()
                .map(ItemDto::new)
                .collect(Collectors.toList()));
    }

    public Item createItem(AppUser currentUser, String storeName, ItemDto itemDto) {
        Store store = storeService.getStore(storeName);
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        Item item = new Item(store, itemDto);
        Schedule schedule = new Schedule(item, itemDto.getAvailabilities());
        item = itemRepository.save(item);
        parameterRepository.saveAll(item.getCustomAttributeList());
        return item;
    }

    public ItemDto updateItem(AppUser currentUser, Long itemId, ItemDto itemDto) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        Item newItem = new Item(store, itemDto);
        return new ItemDto(itemRepository.save(newItem));
    }

    public ItemDto activateItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item.setActive(true);
        return new ItemDto(itemRepository.save(item));
    }

    public ItemDto deactivateItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item.setActive(false);
        return new ItemDto(itemRepository.save(item));
    }

    public void deleteItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        if (!item.getReservations().isEmpty()) {
            throw new RuntimeException("Can't delete item with reservations");
        }
        itemRepository.deleteById(itemId);
    }
}
