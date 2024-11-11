package ru.andreyszdlv.postservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.andreyszdlv.postservice.client.UserServiceClient;
import ru.andreyszdlv.postservice.dto.controller.comment.CommentResponseDTO;
import ru.andreyszdlv.postservice.exception.AnotherUsersCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.mapper.CommentMapper;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.CommentRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    CommentRepo commentRepository;

    @Mock
    PostRepo postRepository;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Mock
    MeterRegistry meterRegistry;

    @Mock
    Counter counter;

    @Mock
    CommentMapper commentMapper;

    @Mock
    PostValidationService postValidationService;

    @InjectMocks
    CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_Success_WhenPostExists() {
        long userId = 1L;
        long postId = 10L;
        String content = "Content";
        String email = "email@email.com";
        Post post = new Post();
        post.setUserId(2L);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());
        CommentResponseDTO expectedResponse = new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getDateCreate(),
                comment.getUserId(),
                comment.getPostId()
        );
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userServiceClient.getUserEmailByUserId(post.getUserId())).thenReturn(ResponseEntity.ok(email));
        when(meterRegistry.counter("comments_per_post", List.of(Tag.of("post_id",String.valueOf(postId))))).thenReturn(counter);
        when(commentMapper.commentToCommentReponseDTO(comment)).thenReturn(expectedResponse);

        CommentResponseDTO result = commentService.createComment(userId, postId, content);

        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(kafkaProducerService, times(1)).sendCreateCommentEvent(eq(email), eq(content));
        assertEquals(expectedResponse, result);
    }

    @Test
    void createComment_ThrowsException_WhenPostNotExist() {
        long userId = 1L;
        long postId = 10L;
        String content = "Content";
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(
                NoSuchPostException.class,
                () -> commentService.createComment(userId, postId, content)
        );

        verify(commentRepository, never()).save(any());
        verify(kafkaProducerService, never()).sendCreateCommentEvent(anyString(), anyString());
    }

    @Test
    void deleteComment_Success_WhenCommentExistsAndCommentCreateUser(){
        long userId = 1L;
        long commentId = 10L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setUserId(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        commentService.deleteComment(userId, commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }


    @Test
    void deleteComment_ThrowsException_WhenCommentNotExist(){
        long userId = 1L;
        long commentId = 10L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchCommentException.class,
                () -> commentService.deleteComment(userId, commentId)
        );

        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void deleteComment_ThrowsException_WhenCommentNoCreateUser(){
        long userId = 1L;
        long commentId = 10L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setUserId(2L);
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        assertThrows(
                AnotherUsersCommentException.class,
                () -> commentService.deleteComment(userId, commentId)
        );

        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void updateComment_Success_WhenCommentExistsAndCommentCreateUser(){
        long userId = 1L;
        long commentId = 10L;
        String oldContent = "old content";
        String newContent = "new content";
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setUserId(userId);
        comment.setContent(oldContent);
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        commentService.updateComment(userId, commentId, newContent);

        assertEquals(newContent, comment.getContent());
    }

    @Test
    void updateComment_ThrowsException_WhenCommentNotExist(){
        long userId = 1L;
        long commentId = 10L;
        String newContent = "new content";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchCommentException.class,
                () -> commentService.updateComment(userId, commentId, newContent)
        );
    }

    @Test
    void updateComment_ThrowsException_WhenCommentNoCreateUser(){
        long userId = 1L;
        long commentId = 10L;
        String oldContent = "old content";
        String newContent = "new content";
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setUserId(2L);
        comment.setContent(oldContent);
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        assertThrows(
                AnotherUsersCommentException.class,
                () -> commentService.updateComment(userId, commentId, newContent)
        );

        assertNotEquals(newContent, comment.getContent());
    }
}