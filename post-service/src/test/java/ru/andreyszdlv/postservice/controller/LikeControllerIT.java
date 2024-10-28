package ru.andreyszdlv.postservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.configuration.KafkaProducerConfig;
import ru.andreyszdlv.postservice.model.Like;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.LikeRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;
import ru.andreyszdlv.postservice.service.KafkaProducerService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class LikeControllerIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepo postRepository;

    @Autowired
    LikeRepo likeRepository;

    @MockBean
    KafkaProducerConfig kafkaProducerConfig;

    @MockBean
    UserServiceFeignClient userServiceFeignClient;

    @MockBean
    KafkaProducerService kafkaProducerService;

    String BASE_URL = "/api/posts";

    @Test
    @Transactional
    void createLike_Returns201_WhenPostExistsAndNoLikeThisUser() throws Exception{
        long userId = 1L;
        long authorPostId = 2L;
        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent("content");
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{postId}/like", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");
        when(userServiceFeignClient.getUserEmailByUserId(authorPostId))
                .thenReturn(ResponseEntity.of(Optional.of("email@email.com")));
        when(userServiceFeignClient.getNameByUserId(userId))
                .thenReturn(ResponseEntity.of(Optional.of("name")));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
        assertTrue(likeRepository.existsByPostIdAndUserId(postId, userId));
    }

    @Test
    @Transactional
    void createLike_Returns409_WhenPostExistsAndAlreadyLikeUserThisPost() throws Exception{
        long userId = 1L;
        long authorPostId = 2L;

        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent("content");
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();

        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        likeRepository.save(like);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{postId}/like", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void createLike_Returns404_WhenNoSuchPost() throws Exception{
        long userId = 1L;
        long authorPostId = 2L;
        long postId = 3L;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{postId}/like", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        assertFalse(likeRepository.existsByPostIdAndUserId(postId, userId));
    }

    @Test
    @Transactional
    void deleteLike_Returns204_WhenPostExistsAndUserLikeThisPost() throws Exception{
        long userId = 1L;
        long authorPostId = 2L;

        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent("content");
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();

        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        likeRepository.save(like);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/{postId}/like", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertFalse(likeRepository.existsByPostIdAndUserId(postId, userId));
    }

    @Test
    void deleteLike_Returns404_WhenNoSuchPost() throws Exception{
        long userId = 1L;
        long postId = 3L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/{postId}/like", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void deleteLike_Returns404_WhenPostExistsAndUserNoLikeThisPost() throws Exception{
        long userId = 1L;
        long authorPostId = 2L;
        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent("content");
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/{postId}/like", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }
}