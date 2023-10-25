package io.github.reconsolidated.zpibackend.features.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {
    private final Long id;
    private final Long userId;
    private final Long itemId;
    private final String nickname;
    private final String content;
    private final LocalDateTime datetime;
}
