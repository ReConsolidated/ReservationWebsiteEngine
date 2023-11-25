package io.github.reconsolidated.zpibackend.features.store;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.features.store.dtos.StoreNameDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/stores", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class StoreController {
    private final StoreService storeService;


    @GetMapping("/{storeName}")
    public ResponseEntity<?> getStore(@CurrentUser AppUser currentUser, @PathVariable String storeName) {
        return ResponseEntity.ok(storeService.getStore(storeName));
    }

    @GetMapping
    public ResponseEntity<List<StoreNameDto>> listStores(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(storeService.listStores(currentUser).stream().map(StoreNameDto::new).toList());
    }
}
