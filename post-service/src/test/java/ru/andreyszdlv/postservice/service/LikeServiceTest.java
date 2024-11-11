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
import ru.andreyszdlv.postservice.exception.AlreadyLikedException;
import ru.andreyszdlv.postservice.exception.NoLikedPostThisUserException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Like;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.LikeRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LikeServiceTest {

    @Mock
    LikeRepo likeRepository;

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
    PostValidationService postValidationService;

    @InjectMocks
    LikeService likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLike_Success_WhenPostExists(){
        long userId = 1L;
        long authorPostId = 2L;
        long postId = 10L;
        String email = "email@email.com";
        Post post = new Post();
        post.setId(postId);
        post.setUserId(authorPostId);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);
        when(userServiceClient.getUserEmailByUserId(authorPostId))
                .thenReturn(ResponseEntity.ok(email));
        when(meterRegistry.counter(
                "likes_per_post",
                List.of(Tag.of("post_id", String.valueOf(postId))))
        ).thenReturn(counter);

        likeService.createLike(userId, postId);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(kafkaProducerService, times(1)).sendCreateLikeEvent(email);
    }

    @Test
    void createLike_ThrowsException_WhenPostNotExist(){
        long userId = 1L;
        long postId = 10L;
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(
                NoSuchPostException.class,
                ()->likeService.createLike(userId, postId)
        );

        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void createLike_ThrowsException_WhenLikeAlreadyExists(){
        long userId = 1L;
        long postId = 10L;
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        assertThrows(
                AlreadyLikedException.class,
                () -> likeService.createLike(userId, postId)
        );

        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void deleteLike_Success_WhenLikeExistsAndPostExists(){
        long userId = 1L;
        long postId = 10L;
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.deleteByPostIdAndUserId(postId, userId)).thenReturn(1);

        likeService.deleteLike(userId, postId);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void deleteLike_ThrowsException_WhenPostNotExists(){
        long userId = 1L;
        long postId = 10L;
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(
                NoSuchPostException.class,
                ()->likeService.deleteLike(userId, postId)
        );

        verify(likeRepository, never()).deleteByPostIdAndUserId(postId, userId);
    }


    @Test
    void deleteLike_ThrowsException_WhenLikeNotExists(){
        long userId = 1L;
        long postId = 10L;
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.deleteByPostIdAndUserId(postId, userId)).thenReturn(0);

        assertThrows(
                NoLikedPostThisUserException.class,
                ()->likeService.deleteLike(userId, postId)
        );

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }
}