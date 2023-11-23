package io.github.reconsolidated.zpibackend.features.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.reconsolidated.zpibackend.features.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import io.github.reconsolidated.zpibackend.features.reservation.Reservation;
import io.github.reconsolidated.zpibackend.features.reservation.Schedule;
import io.github.reconsolidated.zpibackend.features.store.Store;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @JsonDeserialize(as = Long.class)
    @GeneratedValue(generator = "item_generator")
    private Long itemId;
    @ManyToOne
    private Store store;
    private Boolean active;
    private String title;
    private String subtitle;
    private String description;
    private String image;
    @OneToOne(cascade = CascadeType.ALL)
    private Schedule schedule;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Parameter> customAttributeList;
    private Integer amount;
    @OneToMany(cascade = CascadeType.ALL)
    private List<SubItem> subItems;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    public Item(Store store, ItemDto itemDto) {
        this.store = store;
        this.active = itemDto.getActive();
        this.title = itemDto.getTitle();
        this.subtitle = itemDto.getSubtitle();
        this.description = itemDto.getDescription();
        this.image = itemDto.getImage();
        this.amount = itemDto.getAmount();
        this.customAttributeList = itemDto.getCustomAttributeList();
        this.schedule = new Schedule(this, itemDto.getAvailabilities());
    }

}
