package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.exceptions.NoAccessException;
import io.github.reconsolidated.zpibackend.features.store.Store;
import io.github.reconsolidated.zpibackend.features.storeAccess.StoreAccessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final StoreAccessService storeAccessService;
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow();
    }

    public ItemDto getItemDto(AppUser currentUser, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        Store store = item.getStore();
        boolean hasAccess = storeAccessService.validateClientAccess(currentUser, store);
        if (!hasAccess) throw new NoAccessException();

    }
}
