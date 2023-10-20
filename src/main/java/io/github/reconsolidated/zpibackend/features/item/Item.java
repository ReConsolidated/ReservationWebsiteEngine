package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.features.parameter.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long itemId;
    private Boolean active;
    private String title;
    private  String subtitle;
    private String description;
    private String image;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Parameter> customAtributeList;
    private Integer capacity;
    private Integer quantity;
    private LocalDateTime rentalStart;
    private LocalDateTime rentalEnd;


}
