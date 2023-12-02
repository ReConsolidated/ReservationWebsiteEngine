package io.github.reconsolidated.zpibackend.comments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.appUser.AppUserRole;
import io.github.reconsolidated.zpibackend.domain.comment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private AppUser currentUser;
    private CommentDto commentDto;
    private Comment comment;
    private Long itemId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        currentUser = new AppUser();
        currentUser.setId(1L);
        currentUser.setRole(AppUserRole.USER);
        commentDto = new CommentDto();
        comment = Comment.builder()
                .user(currentUser)
                .id(1L)
                .build();

        itemId = 1L;

        when(commentMapper.toComment(any(CommentDto.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);
    }

    @Test
    void addComment_ShouldAddComment_WhenValidDataProvided() {
        commentDto.setUserId(currentUser.getId());
        commentDto.setItemId(itemId);

        CommentDto result = commentService.addComment(currentUser, itemId, commentDto);

        assertNotNull(result);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowException_WhenUserIdDoesNotMatch() {
        commentDto.setUserId(currentUser.getId() + 1);
        commentDto.setItemId(itemId);

        assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment(currentUser, itemId, commentDto);
        });
    }

    @Test
    void addComment_ShouldThrowException_WhenItemIdDoesNotMatch() {
        commentDto.setUserId(currentUser.getId());
        commentDto.setItemId(itemId + 1);

        assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment(currentUser, itemId, commentDto);
        });
    }

    @Test
    void getComments_ShouldReturnCommentDtos_WhenItemIdProvided() {
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = Arrays.asList(comment1, comment2);
        when(commentRepository.findByItem_ItemId(itemId)).thenReturn(comments);
        List<CommentDto> result = commentService.getComments(itemId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(commentRepository).findByItem_ItemId(itemId);
        verify(commentMapper, times(2)).toCommentDto(any(Comment.class));
    }

    @Test
    void deleteComment_ShouldReturnTrue_WhenCommentExistsAndUserIsOwner() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(currentUser, itemId, commentId);

        assertTrue(result);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void deleteComment_ShouldReturnFalse_WhenCommentDoesNotExist() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        boolean result = commentService.deleteComment(currentUser, itemId, commentId);

        assertFalse(result);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteComment_ShouldReturnFalse_WhenUserIsNotOwner() {
        Long commentId = 1L;
        AppUser differentUser = new AppUser();
        differentUser.setId(currentUser.getId() + 1);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(differentUser, itemId, commentId);

        assertFalse(result);
        verify(commentRepository, never()).deleteById(anyLong());
    }
}
