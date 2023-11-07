package io.github.reconsolidated.zpibackend.features.store;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.features.store.dtos.CreateStoreDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/store", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<?> createStore(@CurrentUser AppUser currentUser, @RequestBody CreateStoreDto dto) {
        return ResponseEntity.ok(storeService.createStore(currentUser, dto));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStore(@CurrentUser AppUser currentUser, @RequestParam Long storeId) {
        return ResponseEntity.ok(storeService.getStore(currentUser, storeId));
    }

    @GetMapping
    public ResponseEntity<?> listStores(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(storeService.listStores(currentUser));
    }
}
