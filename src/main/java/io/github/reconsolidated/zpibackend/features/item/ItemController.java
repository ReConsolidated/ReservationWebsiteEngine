package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemListDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.StoreConfigsListDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/stores/{storeId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ItemController {
    private final ItemService itemService;
    @GetMapping
    public ResponseEntity<ItemListDto> listItems(@CurrentUser AppUser currentUser,
                                                 @PathVariable Long storeId) {
        return ResponseEntity.ok(itemService.getItems(currentUser, storeId));
    }

    @PostMapping
    public ResponseEntity<Long> createItem(@CurrentUser AppUser currentUser,
                                           @PathVariable Long storeId,
                                           @RequestBody ItemDto item) {
        Item result = itemService.createItem(currentUser, storeId, item);
        return ResponseEntity.ok(result.getItemId());
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@CurrentUser AppUser currentUser,
                                        @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId));
    }

}
