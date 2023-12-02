package io.github.reconsolidated.zpibackend.domain.storeConfig;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationConfig {

    @Id
    @GeneratedValue(generator = "auth_config_generator")
    private Long id;
    @Builder.Default
    @ElementCollection
    private List<String> requiredPersonalData = new ArrayList<>();
    @Builder.Default
    private Boolean confirmationRequired = false;
    @Builder.Default
    private Boolean isPrivate = false;
}
