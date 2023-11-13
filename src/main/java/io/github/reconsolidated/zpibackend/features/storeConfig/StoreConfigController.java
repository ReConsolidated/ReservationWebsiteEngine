package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/store-configs", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreConfigController {
    private final StoreConfigService storeConfigService;
    private final AppUserService appUserService;
    @PostMapping
    public ResponseEntity<Long> createStoreConfig(@CurrentUser AppUser currentUser, @RequestBody StoreConfig storeConfig) {
        StoreConfig result = storeConfigService.createStoreConfig(currentUser, storeConfig);
        return ResponseEntity.ok(result.getStoreConfigId());
    }

    @GetMapping("/{storeConfigId}")
    public ResponseEntity<StoreConfig> getStoreConfig(@CurrentUser AppUser currentUser, @PathVariable Long storeConfigId) {
        return ResponseEntity.ok(storeConfigService.getStoreConfig(currentUser, storeConfigId));
    }

    @GetMapping
    public ResponseEntity<StoreConfigsListDto> listConfigs(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(storeConfigService.listStoreConfigs(currentUser));
    }

    @GetMapping("/{storeConfigId}/owner")
    public ResponseEntity<OwnerDto> getOwner(@CurrentUser AppUser currentUser, @PathVariable Long storeConfigId) {
        Owner owner = storeConfigService.getStoreConfig(currentUser, storeConfigId).getOwner();
        AppUser appUser = appUserService.getUser(owner.getAppUserId());
        return ResponseEntity.ok(new OwnerDto(owner, appUser));
    }

    @PutMapping("/{storeConfigId}")
    public ResponseEntity<?> updateStoreConfig(@CurrentUser AppUser currentUser, @RequestBody StoreConfig storeConfig) {
        storeConfigService.updateStoreConfig(currentUser, storeConfig);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{storeConfigId}/mainPageConfig")
    public ResponseEntity<StoreConfig> updateMainPageConfig(@CurrentUser AppUser currentUser,
                                                  @PathVariable Long storeConfigId,
                                                  @RequestBody MainPageConfig mainPageConfig) {
        StoreConfig config = storeConfigService.updateMainPageConfig(currentUser, storeConfigId, mainPageConfig);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/{storeConfigId}/mainPageConfig")
    public ResponseEntity<MainPageConfig> getMainPageConfig(@CurrentUser AppUser currentUser,
                                                            @PathVariable Long storeConfigId) {
        MainPageConfig config = storeConfigService.getMainPageConfig(currentUser, storeConfigId);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{storeConfigId}/detailsPageConfig")
    public ResponseEntity<StoreConfig> updateDetailsPageConfig(@CurrentUser AppUser currentUser,
                                                            @PathVariable Long storeConfigId,
                                                            @RequestBody DetailsPageConfig detailsPageConfig) {
        StoreConfig config = storeConfigService.updateDetailsPageConfig(currentUser, storeConfigId, detailsPageConfig);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/{storeConfigId}/detailsPageConfig")
    public ResponseEntity<DetailsPageConfig> getDetailsPageConfig(@CurrentUser AppUser currentUser,
                                                            @PathVariable Long storeConfigId) {
        DetailsPageConfig config = storeConfigService.getDetailsPageConfig(currentUser, storeConfigId);
        return ResponseEntity.ok(config);
    }

}
