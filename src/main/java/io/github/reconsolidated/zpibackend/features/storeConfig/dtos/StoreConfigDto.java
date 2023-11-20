package io.github.reconsolidated.zpibackend.features.storeConfig.dtos;

import io.github.reconsolidated.zpibackend.features.parameter.ParameterSettings;
import io.github.reconsolidated.zpibackend.features.storeConfig.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreConfigDto {
    private Long storeConfigId;
    private OwnerDto owner;
    private CoreConfig core;
    private MainPageConfig mainPage;
    private DetailsPageConfig detailsPage;
    private List<ParameterSettings> customAttributesSpec;
    private AuthenticationConfig authConfig;

}
