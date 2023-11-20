package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.OwnerDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigsListDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/store-configs", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreConfigController {
    private final StoreConfigService storeConfigService;
    private final AppUserService appUserService;
    @PostMapping
    public ResponseEntity<Long> createStoreConfig(@CurrentUser AppUser currentUser, @RequestBody StoreConfigDto storeConfig) {
        StoreConfig result = storeConfigService.createStoreConfig(currentUser, storeConfig);
        return ResponseEntity.ok(result.getStoreConfigId());
    }

    @GetMapping
    public ResponseEntity<StoreConfigsListDto> listConfigs(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(storeConfigService.listStoreConfigs(currentUser));
    }

    @GetMapping("/{storeConfigName}/owner")
    public ResponseEntity<OwnerDto> getOwner(@CurrentUser AppUser currentUser, @PathVariable String storeConfigName) {
        Owner owner = storeConfigService.getStoreConfig(currentUser, storeConfigName).getOwner();
        AppUser appUser = appUserService.getUser(owner.getAppUserId());
        return ResponseEntity.ok(new OwnerDto(owner));
    }

    @GetMapping("/{storeConfigName}")
    public ResponseEntity<StoreConfigDto> getStoreConfigDto(@CurrentUser AppUser currentUser, @PathVariable String storeConfigName) {
        StoreConfigDto config = storeConfigService.getStoreConfigDto(currentUser, storeConfigName);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{storeConfigName}")
    public ResponseEntity<?> updateStoreConfig(@CurrentUser AppUser currentUser, @RequestBody StoreConfig storeConfig) {
        storeConfigService.updateStoreConfig(currentUser, storeConfig);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{storeConfigName}/mainPageConfig")
    public ResponseEntity<StoreConfig> updateMainPageConfig(@CurrentUser AppUser currentUser,
                                                  @PathVariable String storeConfigName,
                                                  @RequestBody MainPageConfig mainPageConfig) {
        StoreConfig config = storeConfigService.updateMainPageConfig(currentUser, storeConfigName, mainPageConfig);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/{storeConfigName}/mainPageConfig")
    public ResponseEntity<MainPageConfig> getMainPageConfig(@CurrentUser AppUser currentUser,
                                                            @PathVariable String storeConfigName) {
        MainPageConfig config = storeConfigService.getMainPageConfig(currentUser, storeConfigName);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{storeConfigName}/detailsPageConfig")
    public ResponseEntity<StoreConfig> updateDetailsPageConfig(@CurrentUser AppUser currentUser,
                                                            @PathVariable String storeConfigName,
                                                            @RequestBody DetailsPageConfig detailsPageConfig) {
        StoreConfig config = storeConfigService.updateDetailsPageConfig(currentUser, storeConfigName, detailsPageConfig);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/{storeConfigName}/detailsPageConfig")
    public ResponseEntity<DetailsPageConfig> getDetailsPageConfig(@CurrentUser AppUser currentUser,
                                                            @PathVariable String storeConfigName) {
        DetailsPageConfig config = storeConfigService.getDetailsPageConfig(currentUser, storeConfigName);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/nameCheck")
    public ResponseEntity<Boolean> checkName(@RequestBody String name) {
        return ResponseEntity.ok(storeConfigService.isNameUnique(name));
    }
}
