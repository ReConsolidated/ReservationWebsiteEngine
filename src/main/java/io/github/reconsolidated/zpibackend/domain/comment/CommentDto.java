package io.github.reconsolidated.zpibackend.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long userId;
    @NotNull
    private Long itemId;
    @NotNull
    private String nickname;
    @NotNull
    private String content;
    @NotNull
    private LocalDateTime datetime;
    private Double rating = 0.0;
}
