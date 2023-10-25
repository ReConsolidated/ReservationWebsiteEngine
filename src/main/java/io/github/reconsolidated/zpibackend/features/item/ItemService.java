package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.storeAccess.StoreAccessService;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreAccessType;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfig;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfigService;
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
        StoreConfig storeConfig = item.getStoreConfig();
        boolean hasAccess = storeAccessService.validateViewAccess(currentUser, storeConfig);
    }
}
