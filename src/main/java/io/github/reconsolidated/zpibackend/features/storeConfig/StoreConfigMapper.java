package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.OwnerDto;
import io.github.reconsolidated.zpibackend.features.storeConfig.dtos.StoreConfigDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StoreConfigMapper {
    private final AppUserService appUserService;

    public StoreConfigDto toDto(StoreConfig storeConfig) {
        OwnerDto ownerDto = new OwnerDto(storeConfig.getOwner());
        return new StoreConfigDto(
                storeConfig.getStoreConfigId(),
                ownerDto,
                storeConfig.getCore(),
                storeConfig.getMainPage(),
                storeConfig.getDetailsPage(),
                storeConfig.getCustomAttributesSpec(),
                storeConfig.getAuthConfig()
        );
    }

    public StoreSummary storeSummary(StoreConfig storeConfig) {
        return storeConfig.getStoreSummary();
    }
}
