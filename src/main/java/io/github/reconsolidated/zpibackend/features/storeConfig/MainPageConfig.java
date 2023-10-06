package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MainPageConfig {

    @Id
    @GeneratedValue(generator = "main_page_config_generator")
    private Long mainPageConfigId;
    private String welcomeTextLine1;
    private String welcomeTextLine2;
    private boolean enableFiltering;
    private boolean showItemTitle;
    private boolean showItemSubtitle;
    private boolean showItemImg;
    private boolean showRating;
}
