package io.github.reconsolidated.zpibackend.features.storeConfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    private String phoneNumber = "";
    private String color = "";
    @JsonProperty("name")
    private String storeName = "";
}
