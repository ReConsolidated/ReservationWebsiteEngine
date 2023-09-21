package io.github.reconsolidated.zpiBackend.features.parameter;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Parameter<T> {

    @Id
    private Long id;
    private String name;
    private T value;
}
