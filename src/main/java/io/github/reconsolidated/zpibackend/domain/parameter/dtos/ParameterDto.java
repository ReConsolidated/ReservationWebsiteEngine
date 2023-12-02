package io.github.reconsolidated.zpibackend.domain.parameter.dtos;

import io.github.reconsolidated.zpibackend.domain.parameter.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto {
    private Long id;
    private Long itemId;
    private String name;
    private String value;

    public ParameterDto(Parameter parameter) {
        this.id = parameter.getId();
        this.itemId = parameter.getItem().getItemId();
        this.name = parameter.getName();
        this.value = parameter.getValue();
    }
}
