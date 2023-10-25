package io.github.reconsolidated.zpibackend.features.item;

import org.springframework.data.jpa.repository.JpaRepository;

interface ItemRepository extends JpaRepository<Item, Long> {
}
