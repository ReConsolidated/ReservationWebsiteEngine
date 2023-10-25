package io.github.reconsolidated.zpibackend.features.storeAccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreAccess {
    @Id
    @GeneratedValue("store_access_generator")
    private Long id;
    private Store
}
