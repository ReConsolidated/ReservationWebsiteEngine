package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/store-configs", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class StoreConfigController {
    private final StoreConfigService storeConfigService;
    @PostMapping
    public ResponseEntity<Long> createStoreConfig(@CurrentUser AppUser currentUser, @RequestBody StoreConfig storeConfig) {
        StoreConfig result = storeConfigService.createStoreConfig(storeConfig);
        return ResponseEntity.ok(result.getStoreConfigId());
    }

    @GetMapping
    public ResponseEntity<StoreConfigsListDto> listConfigs(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(storeConfigService.listStoreConfigs(currentUser));
    }

    @PutMapping("/{storeConfigId}")
    public ResponseEntity<?> updateStoreConfig(@CurrentUser AppUser currentUser, @RequestBody StoreConfig storeConfig) {
        storeConfigService.updateStoreConfig(currentUser, storeConfig);
        return ResponseEntity.ok().build();
    }
}
