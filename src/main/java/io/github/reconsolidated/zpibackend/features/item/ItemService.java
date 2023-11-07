package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemListDto;
import io.github.reconsolidated.zpibackend.features.store.Store;
import io.github.reconsolidated.zpibackend.features.store.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final StoreService storeService;
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow();
    }

    public ItemListDto getItems(AppUser currentUser, Long storeId) {
        Store store = storeService.getStore(currentUser, storeId);
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        return new ItemListDto(itemRepository.findAllByStore_Id(storeId));
    }

    public Item createItem(AppUser currentUser, Long storeId, Item item) {
        Store store = storeService.getStore(currentUser, storeId);
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item.setStore(store);
        return itemRepository.save(item);
    }
}
