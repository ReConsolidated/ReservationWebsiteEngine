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
public class DetailsPageConfig {
    @Id
    @GeneratedValue(generator = "details_page_config_generator")
    private Long detailsPageConfigId;
    private boolean showRating;
    private boolean showComments;
    private boolean showItemDescription;
    private boolean showSubItemTitle;
    private boolean showSubItemSubtitle;
    private boolean reservationConfirmationPrompt;
    private String reservationFailurePrompt;
    private String reservationSummaryPrompt;


}
