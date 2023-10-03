package io.github.reconsolidated.zpibackend.features.parameter;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Parameter {

    @Id
    private Long id;
    private String name;
    private String value;
}
