package io.github.reconsolidated.zpiBackend.features.scheme;

import io.github.reconsolidated.zpiBackend.features.parameter.ParameterSettings;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LayoutConfig {

    private String companyName;
    private String welcomeTextLine1;
    private String welcomeTextLine2;
    private String logoSource;
    private Boolean showLogo;
    private String email;
    private String phoneNumber;
    private Boolean enableFiltering;
    private Boolean enablePossibleValues;
    private List<ParameterSettings> parameterMap;
}
