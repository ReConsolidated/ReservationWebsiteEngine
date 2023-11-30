package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.comment.CommentDto;
import io.github.reconsolidated.zpibackend.domain.comment.CommentService;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ItemMapper {
    @Autowired
    @Lazy
    private CommentService commentService;

    public ItemDto toItemDto(Item item) {
        List<CommentDto> comments = commentService.getComments(item.getItemId());
        Double average = comments.stream()
                .mapToDouble(CommentDto::getRating)
                .average()
                .orElse(0);
        return new ItemDto(
                item,
                average
        );
    }
}
