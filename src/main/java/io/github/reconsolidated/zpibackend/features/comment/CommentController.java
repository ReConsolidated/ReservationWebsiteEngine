package io.github.reconsolidated.zpibackend.features.comment;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
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
        return ResponseEntity.ok(commentService.addComment(currentUser, itemId, commentDto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@CurrentUser AppUser currentUser,
                                           @PathVariable Long itemId,
                                           @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(currentUser, itemId, commentId));
    }
}
