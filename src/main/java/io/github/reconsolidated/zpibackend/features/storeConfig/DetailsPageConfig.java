package io.github.reconsolidated.zpibackend.features.storeConfig;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public class DetailsPageConfig {
    @Id
    @JsonDeserialize(as = Long.class)
    @GeneratedValue(generator = "details_page_config_generator")
    private Long detailsPageConfigId;
    private boolean showRating;
    private boolean showComments;
    private boolean showItemDescription;
    private boolean showSubItemTitle;
    private boolean showSubItemSubtitle;
    private String reservationConfirmationPrompt;
    private String reservationFailurePrompt;
    private String reservationSummaryPrompt;


}
