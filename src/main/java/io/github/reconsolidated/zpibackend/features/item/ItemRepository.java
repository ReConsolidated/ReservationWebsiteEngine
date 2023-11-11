package io.github.reconsolidated.zpibackend.features.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public
interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByStore_Id(Long storeId);
}
