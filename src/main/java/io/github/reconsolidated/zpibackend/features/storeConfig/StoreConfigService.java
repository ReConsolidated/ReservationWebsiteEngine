package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StoreConfigService {
    private final StoreConfigRepository storeConfigRepository;
    private final StoreConfigValidator storeConfigValidator;

    public StoreConfig createStoreConfig(AppUser currentUser, StoreConfig storeConfig) {
        if (storeConfig.getStoreConfigId() != null) {
            throw new IllegalArgumentException("Store Config Id cannot be defined before creating Store Config. " +
                    "Send configuration without Id if you want to create a new Store Config.");
        }
        Owner owner = Owner.builder()
                        .appUserId(currentUser.getId())
                        .build();
        storeConfig.setOwner(owner);

        storeConfigValidator.validateStoreConfig(storeConfig);

        return storeConfigRepository.save(storeConfig);
    }

    public StoreConfigsListDto listStoreConfigs(AppUser currentUser) {
        List<StoreConfig> configList = storeConfigRepository.findByOwner_AppUserId(currentUser.getId());
        return new StoreConfigsListDto(configList);
    }

    public void updateStoreConfig(AppUser currentUser, StoreConfig newStoreConfig) {
        if (newStoreConfig.getStoreConfigId() == null) {
            throw new IllegalArgumentException("Updated Store Config Id cannot be null.");
        }
        StoreConfig currentStoreConfig = storeConfigRepository.findById(newStoreConfig.getStoreConfigId()).orElseThrow();
        if (currentStoreConfig.getOwner() == null || currentStoreConfig.getOwner().getAppUserId() == null
                || !currentStoreConfig.getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not the owner of this Store Config. You cannot edit it.");
        }
        // Core Config cannot be edited
        if (!currentStoreConfig.getCore().equals(newStoreConfig.getCore())) {
            throw new IllegalArgumentException("Core Config cannot be edited");
        }
        storeConfigValidator.validateStoreConfig(newStoreConfig);
        storeConfigRepository.save(newStoreConfig);
    }

    public StoreConfig getStoreConfig(AppUser currentUser, Long storeConfigId) {
        StoreConfig config = storeConfigRepository.findById(storeConfigId).orElseThrow();
        if (config.getOwner() == null || config.getOwner().getAppUserId() == null
            || !config.getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not the owner of this Store Config. You cannot access it.");
        }
        return config;
    }

    public StoreConfig updateMainPageConfig(AppUser currentUser, Long storeConfigId, MainPageConfig mainPageConfig) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigId);
        config.setMainPage(mainPageConfig);
        storeConfigValidator.validateStoreConfig(config);
        storeConfigRepository.save(config);
        return config;
    }

    public StoreConfig updateDetailsPageConfig(AppUser currentUser, Long storeConfigId, DetailsPageConfig detailsPageConfig) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigId);
        config.setDetailsPage(detailsPageConfig);
        storeConfigValidator.validateStoreConfig(config);
        storeConfigRepository.save(config);
        return config;
    }

    public MainPageConfig getMainPageConfig(AppUser currentUser, Long storeConfigId) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigId);
        return config.getMainPage();
    }

    public DetailsPageConfig getDetailsPageConfig(AppUser currentUser, Long storeConfigId) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigId);
        return config.getDetailsPage();
    }
}
