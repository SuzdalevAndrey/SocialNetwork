package ru.andreyszdlv.postservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.andreyszdlv.postservice.dto.controller.post.CreatePostRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.post.UpdatePostRequestDTO;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.mapper.PostMapper;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;
import ru.andreyszdlv.springbootstarters3loadimage.service.ImageService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostServiceTest {

    @Mock
    PostRepo postRepository;

    @Mock
    PostMapper postMapper;

    @Mock
    MeterRegistry meterRegistry;

    @Mock
    Counter counter;

    @Mock
    ImageService imageService;

    @Mock
    PostValidationService postValidationService;

    @InjectMocks
    PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPostsByUserId_ReturnsEmptyList_WhenPostsNotExists() {
        long userId = 1L;
        List<Post> posts = List.of();
        when(postRepository.findAllByUserId(userId)).thenReturn(posts);
        when(postMapper.listPostToListPostResponseDTO(posts)).thenReturn(List.of());

        List<PostResponseDTO> responseDTO = postService.getPostsByUserId(userId);

        assertTrue(responseDTO.isEmpty());
    }

    @Test
    void getPostsByUserId_ReturnsList_WhenPostsExists() {
        long userId = 1L;
        String content1 = "content1";
        Post post1 = new Post();
        post1.setId(1L);
        post1.setUserId(userId);
        post1.setContent(content1);
        post1.setDateCreate(LocalDateTime.now());
        post1.setNumberViews(0L);
        String content2 = "content2";
        Post post2 = new Post();
        post2.setId(2L);
        post2.setUserId(userId);
        post2.setContent(content2);
        post2.setDateCreate(LocalDateTime.now());
        post2.setNumberViews(0L);
        List<Post> posts = List.of(post1, post2);
        List<PostResponseDTO> postsDTO = List.of(
                new PostResponseDTO(
                        post1.getId(),
                        post1.getContent(),
                        post1.getNumberViews(),
                        post1.getDateCreate(),
                        post1.getUserId(),
                        post1.getLikes(),
                        post1.getComments(),
                        post1.getImageIds()
                ),
                new PostResponseDTO(
                        post2.getId(),
                        post2.getContent(),
                        post2.getNumberViews(),
                        post2.getDateCreate(),
                        post2.getUserId(),
                        post2.getLikes(),
                        post2.getComments(),
                        post2.getImageIds()
                )
        );
        when(postRepository.findAllByUserId(userId)).thenReturn(posts);
        when(postMapper.listPostToListPostResponseDTO(posts)).thenReturn(postsDTO);

        List<PostResponseDTO> responseDTO = postService.getPostsByUserId(userId);

        assertEquals(postsDTO.get(0), responseDTO.get(0));
        assertEquals(postsDTO.get(1), responseDTO.get(1));
    }

    @Test
    void createPost_Success_WhenDataIsValid() {
        long userId = 1L;
        String content = "content";
        Post post = new Post();
        post.setId(1L);
        post.setUserId(userId);
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        String imageId1 = "imageId1";
        String imageId2 = "imageId2";
        MockMultipartFile image1 = new MockMultipartFile(
                "image1",
                "avatar.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );
        MockMultipartFile image2 = new MockMultipartFile(
                "image2",
                "avatar.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );
        CreatePostRequestDTO createPostRequestDTO = new CreatePostRequestDTO(
                content,
                List.of(image1, image2)
        );
        PostResponseDTO postDTO = new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getNumberViews(),
                post.getDateCreate(),
                post.getUserId(),
                post.getLikes(),
                post.getComments(),
                List.of(imageId1, imageId2)
        );
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.postToPostResponseDTO(post)).thenReturn(postDTO);
        when(imageService.uploadImage(image1)).thenReturn(imageId1);
        when(imageService.uploadImage(image2)).thenReturn(imageId2);
        when(meterRegistry.counter("posts_per_user", List.of(Tag.of("id", String.valueOf(userId))))).thenReturn(counter);
        when(meterRegistry.counter("comments_per_post", List.of(Tag.of("post_id", String.valueOf(post.getId()))))).thenReturn(counter);
        when(meterRegistry.counter("likes_per_post", List.of(Tag.of("post_id", String.valueOf(post.getId()))))).thenReturn(counter);

        PostResponseDTO responseDTO = postService.createPost(userId, createPostRequestDTO);

        assertEquals(postDTO, responseDTO);
    }

    @Test
    void updatePost_Success_WhenPostExistsAndPostCreateUser() {
        long userId = 1L;
        long postId = 2L;
        String oldContent = "old content";
        String newContent = "new content";
        String imageId1 = "imageId1";
        String imageId2 = "imageId2";
        String imageId3 = "imageId3";
        Post post = new Post();
        post.setId(postId);
        post.setUserId(userId);
        post.setContent(oldContent);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        post.setImageIds(List.of(imageId1));
        MockMultipartFile image2 = new MockMultipartFile(
                "image2",
                "avatar.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );
        MockMultipartFile image3 = new MockMultipartFile(
                "image3",
                "avatar.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );
        UpdatePostRequestDTO updatePostRequestDTO =
                new UpdatePostRequestDTO(newContent, List.of(image2, image3));
        PostResponseDTO expectedPostResponseDTO = PostResponseDTO
                .builder()
                .id(postId)
                .content(newContent)
                .imageIds(List.of(imageId2, imageId3))
                .build();
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);
        when(imageService.uploadImage(image2)).thenReturn(imageId2);
        when(imageService.uploadImage(image3)).thenReturn(imageId3);
        when(postMapper.postToPostResponseDTO(post)).thenReturn(expectedPostResponseDTO);

        PostResponseDTO responseDTO = postService.updatePost(userId, postId, updatePostRequestDTO);

        assertEquals(newContent, post.getContent());
        assertEquals(imageId2, post.getImageIds().get(0));
        assertEquals(imageId3, post.getImageIds().get(1));
        assertEquals(imageId2, responseDTO.imageIds().get(0));
        assertEquals(imageId3, responseDTO.imageIds().get(1));
    }

    @Test
    void updatePost_ThrowsException_WhenPostNotExists() {
        long userId = 1L;
        long postId = 2L;
        String newContent = "new content";
        when(postValidationService.getPostByIdOrThrow(postId)).thenThrow(NoSuchPostException.class);

        assertThrows(
                NoSuchPostException.class,
                () -> postService.updatePost(userId, postId, new UpdatePostRequestDTO(newContent, List.of()))
        );
    }

    @Test
    void deletePost_Success_WhenPostExistsAndPostCreateUser() {
        long userId = 1L;
        long postId = 2L;
        String content = "content";
        Post post = new Post();
        post.setId(postId);
        post.setUserId(userId);
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);

        postService.deletePost(userId, postId);

        verify(postRepository, times(1)).deleteById(postId);
        verify(imageService, never()).deleteImageById(anyString());
    }

    @Test
    void deletePost_ThrowsException_WhenPostNotExists() {
        long userId = 1L;
        long postId = 2L;
        when(postValidationService.getPostByIdOrThrow(postId)).thenThrow(NoSuchPostException.class);

        assertThrows(
                NoSuchPostException.class,
                () -> postService.deletePost(userId, postId)
        );

        verify(postRepository, never()).deleteById(postId);
    }

    @Test
    void getPostByPostId_Success_WhenPostExists() {
        long authorPostId = 2L;
        long postId = 3L;
        String content = "content";
        Post post = new Post();
        post.setId(postId);
        post.setUserId(authorPostId);
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        PostResponseDTO postDTO = new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getNumberViews(),
                post.getDateCreate(),
                post.getUserId(),
                post.getLikes(),
                post.getComments(),
                post.getImageIds()
        );
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);
        when(postMapper.postToPostResponseDTO(post)).thenReturn(postDTO);

        PostResponseDTO responseDTO = postService.getPostByPostId(postId);

        assertEquals(postDTO, responseDTO);
        assertEquals(1, post.getNumberViews());
    }

    @Test
    void getPostByPostId_ThrowsException_WhenPostNotExists() {
        long postId = 3L;
        when(postValidationService.getPostByIdOrThrow(postId)).thenThrow(NoSuchPostException.class);

        assertThrows(
                NoSuchPostException.class,
                () -> postService.getPostByPostId(postId)
        );
    }
}