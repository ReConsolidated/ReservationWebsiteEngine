package io.github.reconsolidated.zpibackend.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByStore_Id(Long storeId);
}
