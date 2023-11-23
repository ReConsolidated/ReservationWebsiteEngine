package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ItemConfig {
    @Id
    @GeneratedValue(generator = "item_config_generator")
    private Long itemConfigId;
    private Boolean itemTitle;
    private Boolean itemSubtitle;
    private Boolean subItemTitle;
    private Boolean subItemSubtitle;
    private Boolean showItemDescription;
    private Boolean commentSection;
    private Boolean enableRating;
    private Boolean showRatingFirstScreen;
    private Boolean showRatingSecondScreen;
    private Boolean showItemImageFirstScreen;
    private Boolean showItemImageSecondScreen;
}
