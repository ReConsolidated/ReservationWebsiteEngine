package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StoreConfigService {
    private final StoreConfigRepository storeConfigRepository;

    public StoreConfig createStoreConfig(StoreConfig storeConfig) {
        if (storeConfig.getStoreConfigId() != null) {
            throw new IllegalArgumentException("Store Config Id cannot be defined before creating Store Config. " +
                    "Send configuration without Id if you want to create a new Store Config.");
        }
        // add validation here
        return storeConfigRepository.save(storeConfig);
    }

    public StoreConfigsListDto listStoreConfigs(AppUser currentUser) {
        // TODO add user personalization
        List<StoreConfig> configList = storeConfigRepository.findAll();
        return new StoreConfigsListDto(configList);
    }
}
