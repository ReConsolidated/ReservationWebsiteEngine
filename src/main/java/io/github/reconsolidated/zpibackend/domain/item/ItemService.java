package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.store.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final StoreService storeService;
    private final ItemMapper itemMapper;

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow();
    }

    public Item getItemFromStore(Long itemId, String storeName) {
        return itemRepository.findByStoreStoreNameAndItemId(storeName, itemId)
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no item with id: %d in store: %s", itemId, storeName)));
    }

    public ItemDto getItemDto(Long itemId) {
        return itemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow());
    }

    public List<ItemDto> getItems(AppUser currentUser, String storeName) {

        Store store = storeService.getStore(storeName);
        if (!Objects.equals(currentUser.getId(), store.getOwnerAppUserId())) {
            throw new IllegalArgumentException("Only owner of a store can see all items!");
        }
        return itemRepository.findAllByStore_Id(store.getId()).stream().map(itemMapper::toItemDto).toList();
    }

    public List<ItemDto> getFilteredItems(String storeName) {

        Store store = storeService.getStore(storeName);
        List<Item> ite = itemRepository.findAllByStore_Id(store.getId());
        return ite
                .stream()
                .filter(item -> item.getActive() && !item.isFixedPast())
                .map(itemMapper::toItemDto)
                .toList();
    }

    public Item createItem(AppUser currentUser, String storeName, ItemDto itemDto) {
        Store store = storeService.getStore(storeName);
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        Item item = new Item(store, itemDto);
        return itemRepository.save(item);
    }

    public ItemDto updateItem(AppUser currentUser, Long itemId, ItemDto itemDto) {
        Item item = getItem(itemId);
        List<Reservation> reservations = item.getReservations();
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item = new Item(store, itemDto);
        item.setItemId(itemId);
        item.setReservations(reservations);
        itemRepository.save(item);
        return itemDto;
    }

    public ItemDto activateItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item.setActive(true);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto deactivateItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item.setActive(false);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public void deleteItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        if (!item.getReservations().isEmpty()) {
            for (Reservation reservation : item.getReservations()) {
                if (reservation.getEndDateTime().isAfter(LocalDateTime.now())) {
                    throw new RuntimeException("Can't delete item with reservations");
                }
            }
        }
        itemRepository.deleteById(itemId);
    }
}
