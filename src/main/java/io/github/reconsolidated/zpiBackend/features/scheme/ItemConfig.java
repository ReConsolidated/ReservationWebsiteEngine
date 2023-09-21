package io.github.reconsolidated.zpiBackend.features.scheme;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemConfig {

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
