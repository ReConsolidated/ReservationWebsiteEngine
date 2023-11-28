package io.github.reconsolidated.zpibackend.domain.comment;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<CommentDto> getComments(Long itemId) {
        List<Comment> comments = commentRepository.findByItem_ItemId(itemId);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(AppUser currentUser, Long itemId, CommentDto commentDto) {
        if (!commentDto.getUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Can't add comment for a different user");
        }
        if (!commentDto.getItemId().equals(itemId)) {
            throw new IllegalArgumentException("Can't add comment with different item id than in path");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    public boolean deleteComment(AppUser currentUser, Long itemId, Long commentId) {
        var comment = commentRepository.findById(commentId);
        if (comment.isEmpty() || !comment.get().getUser().getId().equals(currentUser.getId())) {
            return false;
        }
        commentRepository.deleteById(commentId);
        return true;
    }
}
