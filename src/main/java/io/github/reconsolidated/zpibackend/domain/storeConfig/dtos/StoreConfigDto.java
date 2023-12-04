package io.github.reconsolidated.zpibackend.domain.storeConfig.dtos;

import io.github.reconsolidated.zpibackend.domain.parameter.ParameterSettings;
import io.github.reconsolidated.zpibackend.domain.parameter.dtos.ParameterSettingsDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreConfigDto {
    private Long storeConfigId;
    private OwnerDto owner;
    private CoreConfig core;
    private MainPageConfig mainPage;
    private DetailsPageConfig detailsPage;
    private List<ParameterSettingsDto> customAttributesSpec;
    private AuthenticationConfig authConfig;
    private String storeName;

    public StoreConfigDto(StoreConfig storeConfig) {
        storeConfigId = storeConfig.getStoreConfigId();
        owner = new OwnerDto(storeConfig.getOwner());
        core = storeConfig.getCore();
        mainPage = storeConfig.getMainPage();
        detailsPage = storeConfig.getDetailsPage();
        customAttributesSpec = storeConfig.getCustomAttributesSpec()
                .stream()
                .map(ParameterSettingsDto::new)
                .toList();
        authConfig = storeConfig.getAuthConfig();
    }

}
