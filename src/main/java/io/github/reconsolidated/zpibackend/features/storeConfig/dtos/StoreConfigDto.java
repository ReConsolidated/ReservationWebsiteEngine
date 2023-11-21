package io.github.reconsolidated.zpibackend.features.storeConfig.dtos;

import io.github.reconsolidated.zpibackend.features.parameter.ParameterSettings;
import io.github.reconsolidated.zpibackend.features.storeConfig.*;
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
    private List<ParameterSettings> customAttributesSpec;
    private AuthenticationConfig authConfig;

}
