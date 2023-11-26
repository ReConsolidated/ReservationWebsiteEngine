package io.github.reconsolidated.zpibackend.features.store;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/stores", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class StoreController {
    private final StoreService storeService;


    @GetMapping("/{storeName}")
    public ResponseEntity<?> getStore(@PathVariable String storeName) {
        return ResponseEntity.ok(storeService.getStore(storeName));
    }

    @GetMapping
    public ResponseEntity<?> listStores(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(storeService.listStores(currentUser));
    }
}
