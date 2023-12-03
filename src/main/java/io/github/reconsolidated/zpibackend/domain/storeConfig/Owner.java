package io.github.reconsolidated.zpibackend.domain.storeConfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Owner {

    @Id
    @GeneratedValue(generator = "owner_generator")
    private Long ownerId;
    private Long appUserId;
    private String email = "";
    private String imageUrl = "";
    private String phone = "";
    private String color = "";
    @JsonProperty("name")
    private String storeName = "";
}
