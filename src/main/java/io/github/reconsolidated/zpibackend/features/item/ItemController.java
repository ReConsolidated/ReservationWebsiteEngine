package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemListDto;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemStatus;
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

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@CurrentUser AppUser currentUser,
                                        @PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto) {

        return ResponseEntity.ok(itemService.updateItem(currentUser, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@CurrentUser AppUser currentUser,
                                        @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@CurrentUser AppUser currentUser,
                                        @PathVariable Long itemId) {
        itemService.deleteItem(currentUser, itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{itemId}/activate")
    public ResponseEntity<?> activateItem(@CurrentUser AppUser currentUser,
                                          @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.activateItem(currentUser, itemId));
    }

    @PostMapping("/{itemId}/deactivate")
    public ResponseEntity<?> deactivateItem(@CurrentUser AppUser currentUser,
                                            @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.deactivateItem(currentUser, itemId));
    }

    @GetMapping("/{itemId}/status")
    public ResponseEntity<ItemStatus> getItemStatus(@CurrentUser AppUser currentUser,
                                                    @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemStatus(itemId));
    }

}
