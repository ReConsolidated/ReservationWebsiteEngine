package io.github.reconsolidated.zpiBackend.domain.item;

import io.github.reconsolidated.zpiBackend.domain.time.TimeSettings;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Map;

@Entity
public class ReservableItem {
    @Id
    @GeneratedValue(generator = "reservable_item_id_generator")
    private Long id;
    @OneToOne
    private TimeSettings timeSettings;
    private int maxReservationLimit;
    private String imageUrl;
    private String description;
    private List<String> categories;
    private int maxCategories;
    private Map<String, Object> additionalProperties;
}
