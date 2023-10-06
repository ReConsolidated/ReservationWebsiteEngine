package io.github.reconsolidated.zpibackend.features.storeConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class Owner {

    @Id
    @GeneratedValue(generator = "owner_config_generator")
    private Long ownerId;
    private String name;
    private String logoSrc;
    private String phone;
    private String email;
}
