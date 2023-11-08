package io.github.reconsolidated.zpibackend.features.parameter;

import io.github.reconsolidated.zpibackend.features.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parameter {

    @Id
    @GeneratedValue(generator = "parameter_ids")
    private Long id;
    private String name;
    private String value;
}
