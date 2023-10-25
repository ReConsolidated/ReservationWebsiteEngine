package io.github.reconsolidated.zpibackend.features.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow();
    }
}
