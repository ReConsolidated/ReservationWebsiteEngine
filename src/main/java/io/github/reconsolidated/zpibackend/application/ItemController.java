package io.github.reconsolidated.zpibackend.application;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.infrastracture.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.item.ItemService;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(value = "/stores/{storeName}/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {
    private final ItemService itemService;
    @GetMapping
    public ResponseEntity<List<ItemDto>> listFilteredItems(@CurrentUser AppUser currentUser,
                                                   @PathVariable String storeName) {
        List<ItemDto> res = itemService.getFilteredItems(currentUser, storeName);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<ItemDto>> listItemsAdmin(@CurrentUser AppUser currentUser,
                                                   @PathVariable String storeName) {
        return ResponseEntity.ok(itemService.getItems(currentUser, storeName));
    }

    @PostMapping
    public ResponseEntity<Long> createItem(@CurrentUser AppUser currentUser,
                                           @PathVariable String storeName,
                                           @RequestBody @Valid ItemDto item) {
        Item result = itemService.createItem(currentUser, storeName, item);
        return ResponseEntity.ok(result.getItemId());
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@CurrentUser AppUser currentUser,
                                        @PathVariable String storeName,
                                        @PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto) {

        return ResponseEntity.ok(itemService.updateItem(currentUser, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@CurrentUser AppUser currentUser,
                                        @PathVariable String storeName,
                                        @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemDto(itemId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@CurrentUser AppUser currentUser,
                                        @PathVariable String storeName,
                                        @PathVariable Long itemId) {
        itemService.deleteItem(currentUser, itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{itemId}/activate")
    public ResponseEntity<?> activateItem(@CurrentUser AppUser currentUser,
                                          @PathVariable String storeName,
                                          @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.activateItem(currentUser, itemId));
    }

    @PostMapping("/{itemId}/deactivate")
    public ResponseEntity<?> deactivateItem(@CurrentUser AppUser currentUser,
                                            @PathVariable String storeName,
                                            @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.deactivateItem(currentUser, itemId));
    }

}
