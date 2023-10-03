package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/store-config", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class StoreConfigController {
    private final StoreConfigService storeConfigService;
    @PostMapping
    public ResponseEntity<Long> createStoreConfig(StoreConfig storeConfig) {
        StoreConfig result = storeConfigService.createStoreConfig(storeConfig);
        return ResponseEntity.ok(result.getStoreConfigId());
    }
}
