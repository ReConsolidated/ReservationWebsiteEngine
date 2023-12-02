package io.github.reconsolidated.zpibackend.domain.storeConfig;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreConfigRepository extends JpaRepository<StoreConfig, Long> {

    List<StoreConfig> findByOwner_AppUserId(Long id);

    Optional<StoreConfig> findByOwnerStoreName(String storeName);

}
