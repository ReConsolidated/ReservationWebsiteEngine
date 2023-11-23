package io.github.reconsolidated.zpibackend.features.parameter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(as = Long.class)
    @GeneratedValue(generator = "parameter_generator")
    private Long id;
    private String name;
    private String value;
}
