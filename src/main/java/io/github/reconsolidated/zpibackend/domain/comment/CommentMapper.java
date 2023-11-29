package io.github.reconsolidated.zpibackend.domain.comment;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.item.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentMapper {
    private final AppUserService appUserService;
    private final ItemService itemService;
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getItem().getItemId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getDateTime(),
                comment.getRating()
                );
    }

    public Comment toComment(CommentDto commentDto) {
        Item item = itemService.getItem(commentDto.getItemId());
        AppUser appUser = appUserService.getUser(commentDto.getUserId());
        return Comment.builder()
                .id(commentDto.getId())
                .content(commentDto.getContent())
                .user(appUser)
                .dateTime(commentDto.getDatetime())
                .item(item)
                .rating(commentDto.getRating())
                .build();
    }
}
