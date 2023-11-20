package io.github.reconsolidated.zpibackend.features.storeConfig;

import com.fasterxml.jackson.annotation.JsonProperty;
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
