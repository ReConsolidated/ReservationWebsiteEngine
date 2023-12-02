package io.github.reconsolidated.zpibackend.application;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.infrastracture.currentUser.CurrentUser;
import io.github.reconsolidated.zpibackend.domain.comment.CommentDto;
import io.github.reconsolidated.zpibackend.domain.comment.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items/{itemId}")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long itemId) {
        return ResponseEntity.ok(commentService.getComments(itemId));
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentDto> addComment(@CurrentUser AppUser currentUser,
                                                 @PathVariable Long itemId,
                                                 @RequestBody CommentDto commentDto) {
        commentDto.setUserId(currentUser.getId());
        return ResponseEntity.ok(commentService.addComment(currentUser, itemId, commentDto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@CurrentUser AppUser currentUser,
                                           @PathVariable Long itemId,
                                           @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(currentUser, itemId, commentId));
    }
}
