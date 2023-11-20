package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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
    private Long id;
    @ElementCollection
    private List<String> requiredPersonalData = new ArrayList<>();
    private Boolean confirmationRequire;
    private Boolean isPrivate;
}
