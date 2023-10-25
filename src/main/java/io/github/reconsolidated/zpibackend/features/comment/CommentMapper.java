package io.github.reconsolidated.zpibackend.features.comment;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.ItemService;
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
                comment.getDateTime()
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
                .build();
    }
}
