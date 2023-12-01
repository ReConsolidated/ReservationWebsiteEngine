package io.github.reconsolidated.zpibackend.application;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.domain.store.dtos.StoreNameDto;
import io.github.reconsolidated.zpibackend.infrastracture.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.domain.store.StoreService;
import io.github.reconsolidated.zpibackend.domain.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.*;
import io.github.reconsolidated.zpibackend.domain.storeConfig.dtos.OwnerDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.dtos.StoreConfigDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.dtos.StoreConfigsListDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/store-configs", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreConfigController {

    private final StoreConfigService storeConfigService;
    private final StoreService storeService;
    private final AppUserService appUserService;
    @PostMapping
    @Transactional
    public ResponseEntity<Long> createStoreConfig(@CurrentUser AppUser currentUser, @RequestBody StoreConfigDto storeConfig) {
        StoreConfig result = storeConfigService.createStoreConfig(currentUser, storeConfig);
        storeService.createStore(currentUser, new CreateStoreDto(result.getStoreConfigId(), result.getName()));
        return ResponseEntity.ok(result.getStoreConfigId());
    }

    @GetMapping
    public ResponseEntity<List<StoreNameDto>> listConfigsSummary(@CurrentUser AppUser currentUser) {
        return ResponseEntity.ok(storeConfigService.listStoreConfigsSummary(currentUser));
    }

    @GetMapping("/details")
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
    public ResponseEntity<StoreConfigDto> getStoreConfigDto(@PathVariable String storeConfigName) {
        StoreConfigDto config = storeConfigService.getStoreConfigDto(storeConfigName);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{storeConfigName}")
    public ResponseEntity<?> updateStoreConfig(@CurrentUser AppUser currentUser,
                                               @RequestBody StoreConfig storeConfig,
                                               @PathVariable String storeConfigName) {
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

    @PostMapping("{storeConfigName}/image_url")
    public ResponseEntity<StoreConfig> setImageUrl(@CurrentUser AppUser user,
                                             @PathVariable String storeConfigName,
                                             @RequestParam String imageUrl) {
        return ResponseEntity.ok(storeConfigService.updateImageUrl(user, storeConfigName, imageUrl));
    }

    @PostMapping("{storeConfigName}/color")
    public ResponseEntity<StoreConfig> setColor(@CurrentUser AppUser user,
                                          @PathVariable String storeConfigName,
                                          @RequestParam String color) {
        return ResponseEntity.ok(storeConfigService.updateColor(user, storeConfigName, color));
    }

    @GetMapping("/nameCheck")
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        return ResponseEntity.ok(storeConfigService.isNameUnique(name));
    }
}
