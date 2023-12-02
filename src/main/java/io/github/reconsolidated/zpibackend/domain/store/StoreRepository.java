package io.github.reconsolidated.zpibackend.domain.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findAllByOwnerAppUserId(Long id);

    Optional<Store> findByStoreConfigName(String name);
}
