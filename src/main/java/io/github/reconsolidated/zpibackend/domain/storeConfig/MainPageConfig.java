package io.github.reconsolidated.zpibackend.domain.storeConfig;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
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
