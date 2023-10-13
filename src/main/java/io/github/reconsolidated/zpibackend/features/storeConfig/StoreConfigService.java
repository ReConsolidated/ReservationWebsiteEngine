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

    public StoreConfig createStoreConfig(StoreConfig storeConfig) {
        // TODO ZPI-59 add Owner
        if (storeConfig.getStoreConfigId() != null) {
            throw new IllegalArgumentException("Store Config Id cannot be defined before creating Store Config. " +
                    "Send configuration without Id if you want to create a new Store Config.");
        }

        storeConfigValidator.validateStoreConfig(storeConfig);

        return storeConfigRepository.save(storeConfig);
    }

    public StoreConfigsListDto listStoreConfigs(AppUser currentUser) {
        // TODO ZPI-59 add user personalization
        List<StoreConfig> configList = storeConfigRepository.findAll();
        return new StoreConfigsListDto(configList);
    }

    public void updateStoreConfig(AppUser currentUser, StoreConfig newStoreConfig) {
        // TODO ZPI-59 add user personalization
        if (newStoreConfig.getStoreConfigId() == null) {
            throw new IllegalArgumentException("Updated Store Config Id cannot be null.");
        }
        StoreConfig currentStoreConfig = storeConfigRepository.findById(newStoreConfig.getStoreConfigId()).orElseThrow();
        // Core Config cannot be edited
        if (!currentStoreConfig.getCoreConfig().equals(newStoreConfig.getCoreConfig())) {
            throw new IllegalArgumentException("Core Config cannot be edited");
        }
        // TODO validate
        storeConfigRepository.save(newStoreConfig);
    }
}
