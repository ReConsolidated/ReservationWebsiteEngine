package io.github.reconsolidated.zpibackend.features.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItem_ItemId(Long itemId);
}
