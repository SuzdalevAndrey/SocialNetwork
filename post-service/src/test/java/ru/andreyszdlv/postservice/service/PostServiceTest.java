//package ru.andreyszdlv.postservice.service;
//
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Tag;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
//import ru.andreyszdlv.postservice.exception.AnotherUserCreatePostException;
//import ru.andreyszdlv.postservice.exception.NoSuchPostException;
//import ru.andreyszdlv.postservice.mapper.PostMapper;
//import ru.andreyszdlv.postservice.model.Post;
//import ru.andreyszdlv.postservice.repository.PostRepo;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class PostServiceTest {
//
//    @Mock
//    PostRepo postRepository;
//
//    @Mock
//    PostMapper postMapper;
//
//    @Mock
//    MeterRegistry meterRegistry;
//
//    @Mock
//    Counter counter;
//
//    @InjectMocks
//    PostService postService;
//
//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getPostsByUserId_ReturnsEmptyList_WhenPostsNotExists(){
//        long userId = 1L;
//        List<Post> posts = List.of();
//        when(postRepository.findAllByUserId(userId)).thenReturn(posts);
//        when(postMapper.listPostToListPostResponseDTO(posts)).thenReturn(List.of());
//
//        List<PostResponseDTO> responseDTO = postService.getPostsByUserId(userId);
//
//        assertTrue(responseDTO.isEmpty());
//    }
//
//    @Test
//    void getPostsByUserId_ReturnsList_WhenPostsExists(){
//        long userId = 1L;
//
//        String content1 = "content1";
//        Post post1 = new Post();
//        post1.setId(1L);
//        post1.setUserId(userId);
//        post1.setContent(content1);
//        post1.setDateCreate(LocalDateTime.now());
//        post1.setNumberViews(0L);
//
//        String content2 = "content2";
//        Post post2 = new Post();
//        post2.setId(2L);
//        post2.setUserId(userId);
//        post2.setContent(content2);
//        post2.setDateCreate(LocalDateTime.now());
//        post2.setNumberViews(0L);
//        List<Post> posts = List.of(post1, post2);
//        List<PostResponseDTO> postsDTO = List.of(
//                new PostResponseDTO(
//                        post1.getId(),
//                        post1.getContent(),
//                        post1.getNumberViews(),
//                        post1.getDateCreate(),
//                        post1.getUserId(),
//                        null,
//                        null
//                ),
//                new PostResponseDTO(
//                        post2.getId(),
//                        post2.getContent(),
//                        post2.getNumberViews(),
//                        post2.getDateCreate(),
//                        post2.getUserId(),
//                        null,
//                        null
//                )
//        );
//        when(postRepository.findAllByUserId(userId)).thenReturn(posts);
//        when(postMapper.listPostToListPostResponseDTO(posts)).thenReturn(postsDTO);
//
//        List<PostResponseDTO> responseDTO = postService.getPostsByUserId(userId);
//
//        assertEquals(postsDTO.get(0), responseDTO.get(0));
//        assertEquals(postsDTO.get(1), responseDTO.get(1));
//    }
//
//    @Test
//    void createPost_Success_WhenDataIsValid(){
//        long userId = 1L;
//        String content = "content";
//        Post post = new Post();
//        post.setId(1L);
//        post.setUserId(userId);
//        post.setContent(content);
//        post.setDateCreate(LocalDateTime.now());
////        post.setNumberViews(0L);
////        PostResponseDTO postDTO =  new PostResponseDTO(
////                post.getId(),
////                post.getContent(),
////                post.getNumberViews(),
////                post.getDateCreate(),
////                post.getUserId(),
////                null,
////                null
////        );
//        when(postRepository.save(any(Post.class))).thenReturn(post);
////        when(postMapper.postToPostResponseDTO(post)).thenReturn(postDTO);
//        when(meterRegistry.counter("posts_per_user", List.of(Tag.of("id", String.valueOf(userId))))).thenReturn(counter);
//        when(meterRegistry.counter("comments_per_post", List.of(Tag.of("post_id", String.valueOf(post.getId()))))).thenReturn(counter);
//        when(meterRegistry.counter("likes_per_post", List.of(Tag.of("post_id", String.valueOf(post.getId()))))).thenReturn(counter);
//
////        PostResponseDTO responseDTO = postService.createPost(userId, content);
//
////        assertEquals(postDTO, responseDTO);
//    }
//
//    @Test
//    void updatePost_Success_WhenPostExistsAndPostCreateUser(){
//        long userId = 1L;
//        long postId = 2L;
//        String oldContent = "old content";
//        String newContent = "new content";
//        Post post = new Post();
//        post.setId(postId);
//        post.setUserId(userId);
//        post.setContent(oldContent);
//        post.setDateCreate(LocalDateTime.now());
//        post.setNumberViews(0L);
//        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
//
//        postService.updatePost(userId, postId, newContent);
//
//        assertEquals(newContent, post.getContent());
//    }
//
//    @Test
//    void updatePost_ThrowsException_WhenPostNotExists(){
//        long userId = 1L;
//        long postId = 2L;
//        String newContent = "new content";
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        assertThrows(
//                NoSuchPostException.class,
//                ()->postService.updatePost(userId, postId, newContent)
//        );
//    }
//
//    @Test
//    void updatePost_ThrowsException_WhenPostNoCreateUser(){
//        long userId = 1L;
//        long authorPostId = 2L;
//        long postId = 3L;
//        String oldContent = "old content";
//        String newContent = "new content";
//        Post post = new Post();
//        post.setId(postId);
//        post.setUserId(authorPostId);
//        post.setContent(oldContent);
//        post.setDateCreate(LocalDateTime.now());
//        post.setNumberViews(0L);
//        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
//
//        assertThrows(
//                AnotherUserCreatePostException.class,
//                () -> postService.updatePost(userId, postId, newContent)
//        );
//
//        assertEquals(oldContent, post.getContent());
//    }
//
//    @Test
//    void deletePost_Success_WhenPostExistsAndPostCreateUser(){
//        long userId = 1L;
//        long postId = 2L;
//        String content = "content";
//        Post post = new Post();
//        post.setId(postId);
//        post.setUserId(userId);
//        post.setContent(content);
//        post.setDateCreate(LocalDateTime.now());
//        post.setNumberViews(0L);
//        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
//
//        postService.deletePost(userId, postId);
//
//        verify(postRepository, times(1)).deleteById(postId);
//    }
//
//    @Test
//    void deletePost_ThrowsException_WhenPostNotExists(){
//        long userId = 1L;
//        long postId = 2L;
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        assertThrows(
//                NoSuchPostException.class,
//                ()->postService.deletePost(userId, postId)
//        );
//
//        verify(postRepository, never()).deleteById(postId);
//    }
//
//    @Test
//    void deletePost_ThrowsException_WhenPostNoCreateUser(){
//        long userId = 1L;
//        long authorPostId = 2L;
//        long postId = 3L;
//        String content = "content";
//        Post post = new Post();
//        post.setId(postId);
//        post.setUserId(authorPostId);
//        post.setContent(content);
//        post.setDateCreate(LocalDateTime.now());
//        post.setNumberViews(0L);
//        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
//
//        assertThrows(
//                AnotherUserCreatePostException.class,
//                () -> postService.deletePost(userId, postId)
//        );
//
//        verify(postRepository, never()).deleteById(postId);
//    }
//
//    @Test
//    void getPostByPostId_Success_WhenPostExists(){
//        long authorPostId = 2L;
//        long postId = 3L;
//        String content = "content";
//        Post post = new Post();
//        post.setId(postId);
//        post.setUserId(authorPostId);
//        post.setContent(content);
//        post.setDateCreate(LocalDateTime.now());
//        post.setNumberViews(0L);
//        PostResponseDTO postDTO =  new PostResponseDTO(
//                post.getId(),
//                post.getContent(),
//                post.getNumberViews(),
//                post.getDateCreate(),
//                post.getUserId(),
//                null,
//                null
//        );
//        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
//        when(postMapper.postToPostResponseDTO(post)).thenReturn(postDTO);
//
//        PostResponseDTO responseDTO = postService.getPostByPostId(postId);
//
//        assertEquals(postDTO, responseDTO);
//        assertEquals(1, post.getNumberViews());
//    }
//
//    @Test
//    void getPostByPostId_ThrowsException_WhenPostNotExists(){
//        long postId = 3L;
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        assertThrows(
//                NoSuchPostException.class,
//                ()->postService.getPostByPostId(postId)
//        );
//    }
//}