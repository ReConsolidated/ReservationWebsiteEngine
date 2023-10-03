package io.github.reconsolidated.zpibackend.features.parameter;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class ParameterSettings {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    protected Long id;
    protected String name;
    protected String type;
    protected Boolean isRequired;
    protected Boolean isFilterable;
    protected Boolean showFirstScreen;
    protected Boolean showSecondScreen;
}
