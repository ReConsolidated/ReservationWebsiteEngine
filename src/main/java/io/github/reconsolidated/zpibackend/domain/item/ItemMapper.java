package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.comment.CommentDto;
import io.github.reconsolidated.zpibackend.domain.comment.CommentService;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.DoubleStream;

@Component
@NoArgsConstructor
public class ItemMapper {
    @Autowired
    @Lazy
    private CommentService commentService;

    public ItemDto toItemDto(Item item) {
        List<CommentDto> comments = commentService.getComments(item.getItemId());
        Double average = comments.stream()
                .filter(comment -> comment.getRating() != null && comment.getRating() > 0)
                .mapToDouble(CommentDto::getRating)
                .average()
                .orElse(0);

        long count = comments.stream()
                .filter(comment -> comment.getRating() != null && comment.getRating() > 0)
                .count();
        return new ItemDto(
                item,
                average,
                (int) count
        );
    }
}
