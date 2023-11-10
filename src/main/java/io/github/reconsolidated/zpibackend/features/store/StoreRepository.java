package io.github.reconsolidated.zpibackend.features.store;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findAllByOwnerAppUserId(Long id);
}
