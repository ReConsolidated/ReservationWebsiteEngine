package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.store.StoreService;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigsListDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StoreConfigService {
    private final StoreConfigRepository storeConfigRepository;
    private final StoreConfigValidator storeConfigValidator;
    private final StoreConfigMapper storeConfigMapper;

    public StoreConfig createStoreConfig(AppUser currentUser, StoreConfigDto storeConfigDto) {
        if (storeConfigDto.getStoreConfigId() != null) {
            throw new IllegalArgumentException("Store Config Id cannot be defined before creating Store Config. " +
                    "Send configuration without Id if you want to create a new Store Config.");
        }
        Owner owner = storeConfigDto.getOwner().toOwner(currentUser.getId());

        if (!isNameUnique(owner.getStoreName())) {
            throw new IllegalArgumentException("Store name must be unique! Name: " + owner.getStoreName() +
                    " is not unique.");
        }
        StoreConfig storeConfig = StoreConfig.builder()
                .owner(owner)
                .name(owner.getStoreName().replaceAll("[ /]", "_"))
                .core(storeConfigDto.getCore())
                .mainPage(storeConfigDto.getMainPage())
                .detailsPage(storeConfigDto.getDetailsPage())
                .authConfig(storeConfigDto.getAuthConfig())
                .customAttributesSpec(storeConfigDto.getCustomAttributesSpec())
                .build();

        storeConfigValidator.validateStoreConfig(storeConfig);

        return storeConfigRepository.save(storeConfig);
    }

    public boolean isNameUnique(String storeName) {
        return storeConfigRepository.findByName(storeName.replaceAll("[ /]", "_")).isEmpty();
    }


    public StoreConfigsListDto listStoreConfigs(AppUser currentUser) {
        List<StoreConfig> configList = storeConfigRepository.findByOwner_AppUserId(currentUser.getId());
        return new StoreConfigsListDto(configList.stream().map(storeConfigMapper::toDto).toList());
    }

    public List<StoreSummary> listStoreConfigsSummary(AppUser currentUser) {
        List<StoreConfig> configList = storeConfigRepository.findByOwner_AppUserId(currentUser.getId());
        return configList.stream().map(StoreConfig::getStoreSummary).toList();
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

    public StoreConfig getStoreConfig(AppUser currentUser, String storeConfigName) {
        return storeConfigRepository.findByName(storeConfigName).orElseThrow();
    }

    public StoreConfigDto getStoreConfigDto(AppUser currentUser, String storeConfigName) {
        StoreConfig config = storeConfigRepository.findByName(storeConfigName).orElseThrow();
        if (config.getOwner() == null || config.getOwner().getAppUserId() == null
                || !config.getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not the owner of this Store Config. You cannot access it.");
        }
        return storeConfigMapper.toDto(config);
    }

    public StoreConfig updateMainPageConfig(AppUser currentUser, String storeConfigName, MainPageConfig mainPageConfig) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigName);
        config.setMainPage(mainPageConfig);
        storeConfigValidator.validateStoreConfig(config);
        storeConfigRepository.save(config);
        return config;
    }

    public StoreConfig updateDetailsPageConfig(AppUser currentUser, String storeConfigName, DetailsPageConfig detailsPageConfig) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigName);
        config.setDetailsPage(detailsPageConfig);
        storeConfigValidator.validateStoreConfig(config);
        storeConfigRepository.save(config);
        return config;
    }

    public MainPageConfig getMainPageConfig(AppUser currentUser, String storeConfigName) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigName);
        return config.getMainPage();
    }

    public DetailsPageConfig getDetailsPageConfig(AppUser currentUser, String storeConfigName) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigName);
        return config.getDetailsPage();
    }

    public StoreConfig updateColor(AppUser currentUser, String storeConfigName, String color) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigName);
        config.getOwner().setColor(color);
        return config;
    }

    public StoreConfig updateImageUrl(AppUser currentUser, String storeConfigName, String imageUrl) {
        StoreConfig config = getStoreConfig(currentUser, storeConfigName);
        config.getOwner().setImageUrl(imageUrl);
        return config;
    }
}
