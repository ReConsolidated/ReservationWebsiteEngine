package io.github.reconsolidated.zpibackend.features.storeConfig;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationConfig {

    @Id
    @JsonDeserialize(as = Long.class)
    @GeneratedValue(generator = "auth_config_generator")
    private Long id;
    @ElementCollection
    private List<String> requiredPersonalData = new ArrayList<>();
    private Boolean confirmationRequire;
    private Boolean isPrivate;
}
