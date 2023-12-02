package io.github.reconsolidated.zpibackend.domain.parameter;

import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.parameter.dtos.ParameterDto;
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
    @GeneratedValue(generator = "parameter_generator")
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "item_id")
    private Item item;
    private String name;
    private String value;

    public Parameter(ParameterDto parameterDto, Item item) {
        this.id = parameterDto.getId();
        this.item = item;
        this.name = parameterDto.getName();
        this.value = parameterDto.getValue();
    }
}
