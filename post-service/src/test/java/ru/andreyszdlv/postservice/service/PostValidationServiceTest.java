package ru.andreyszdlv.postservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.postservice.exception.AnotherUserCreatePostException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostValidationServiceTest {

    @Mock
    PostRepo postRepository;

    @InjectMocks
    PostValidationService postValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPostByIdOrThrow_ReturnsPost_WhenPostExist() {
        long postId = 1L;
        Post post = mock(Post.class);
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        Post response = postValidationService.getPostByIdOrThrow(postId);

        assertNotNull(response);
        assertEquals(post, response);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getPostByIdOrThrow_ThrowException_WhenPostNotExist() {
        long postId = 1L;
        when(postRepository.findById(postId)).thenThrow(NoSuchPostException.class);

        assertThrows(
                NoSuchPostException.class,
                () -> postValidationService.getPostByIdOrThrow(postId)
        );

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void validateUserOwnership_Success_WhenUserIsOwner() {
        long userId = 1L;
        long authorPostId = 1L;
        Post post = mock(Post.class);
        when(post.getUserId()).thenReturn(userId);

        postValidationService.validateUserOwnership(post, authorPostId);
    }

    @Test
    void validateUserOwnership_ThrowException_WhenUserIsNotOwner() {
        long userId = 1L;
        long authorPostId = 2L;
        Post post = mock(Post.class);
        when(post.getUserId()).thenReturn(userId);

        assertThrows(
                AnotherUserCreatePostException.class,
                () -> postValidationService.validateUserOwnership(post, authorPostId)
        );
    }

}