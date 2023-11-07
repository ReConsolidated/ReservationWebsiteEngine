package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import io.github.reconsolidated.zpibackend.features.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long itemId;
    @ManyToOne
    private Store store;
    private Boolean active;
    private String title;
    private String subtitle;
    private String description;
    private String image;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Parameter> customAtributeList;
    private Integer capacity;
    private Integer quantity;
    private LocalDateTime rentalStart;
    private LocalDateTime rentalEnd;


}
