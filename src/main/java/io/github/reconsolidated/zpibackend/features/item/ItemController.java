package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    public ResponseEntity<ItemDto> getItem(@CurrentUser @Nullable AppUser currentUser, Long itemId) {
        return ResponseEntity.ok(itemService.getItemDto(currentUser, itemId));
    }
}
