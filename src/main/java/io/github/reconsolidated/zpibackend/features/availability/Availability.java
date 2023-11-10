package io.github.reconsolidated.zpibackend.features.availability;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Availability {
    @Id
    @GeneratedValue
    private Long id;
}
