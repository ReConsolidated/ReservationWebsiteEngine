package io.github.reconsolidated.zpibackend.features.storeConfig;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreConfigRepository extends JpaRepository<StoreConfig, Long> {

    List<StoreConfig> findByOwner_AppUserId(Long id);
}
