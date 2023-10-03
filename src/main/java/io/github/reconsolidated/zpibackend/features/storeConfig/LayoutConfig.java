package io.github.reconsolidated.zpibackend.features.storeConfig;

import io.github.reconsolidated.zpibackend.features.parameter.ParameterSettings;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LayoutConfig {
    @Id
    @GeneratedValue(generator = "layout_config_generator")
    private Long layoutConfigId;
    private String companyName;
    private String welcomeTextLine1;
    private String welcomeTextLine2;
    private String logoSource;
    private Boolean showLogo;
    private String email;
    private String phoneNumber;
    private Boolean enableFiltering;
    private Boolean enablePossibleValues;
    @OneToMany
    private List<ParameterSettings> parameterMap;
}
