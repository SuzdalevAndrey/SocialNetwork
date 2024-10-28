package ru.andreyszdlv.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import ru.andreyszdlv.postservice.dto.controller.comment.CommentResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.comment.CreateCommentRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.comment.UpdateCommentRequestDTO;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.CommentRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;
import ru.andreyszdlv.postservice.service.KafkaProducerService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class CommentControllerIT {

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
    CommentRepo commentRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    static PostRepo postRepository;

    @MockBean
    KafkaProducerService kafkaProducerService;

    @MockBean
    KafkaProducerConfig kafkaProducerConfig;

    @MockBean
    UserServiceFeignClient userServiceFeignClient;

    String BASE_URL = "/api/posts";

    static long postId = 0;

    static long authorPostId = 1L;

    @BeforeAll
    static void setUpBeforeAllTests(@Autowired PostRepo repository){
        postRepository = repository;
        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent("content");
        post.setDateCreate(LocalDateTime.now());
        postId = postRepository.save(post).getId();
    }

    @Test
    @Transactional
    void createComment_Returns201_WhenDataIsValid() throws Exception{
        long userId = 1L;
        String content = "content";
        CreateCommentRequestDTO requestDTO = new CreateCommentRequestDTO(content);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{postId}/comment", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));
        when(userServiceFeignClient.getNameByUserId(userId)).thenReturn(ResponseEntity.of(Optional.of("name")));
        when(userServiceFeignClient.getUserEmailByUserId(authorPostId)).thenReturn(ResponseEntity.of(Optional.of("email@email.com")));

        String response = mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content").value(content),
                        jsonPath("$.id", notNullValue())
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        CommentResponseDTO responseDTO = objectMapper.readValue(response, CommentResponseDTO.class);
        assertEquals(content, responseDTO.content());
        assertEquals(content, commentRepository.findById(responseDTO.id()).get().getContent());
        assertEquals(userId, commentRepository.findById(responseDTO.id()).get().getUserId());
        assertEquals(postId, commentRepository.findById(responseDTO.id()).get().getPostId());
    }

    @Test
    void createComment_Returns400_WhenDataInvalid() throws Exception{
        long userId = 1L;
        String content = "";
        CreateCommentRequestDTO requestDTO = new CreateCommentRequestDTO(content);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{postId}/comment", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    void createComment_Returns404_WhenDataIsValidAndPostNoSuch() throws Exception{
        long userId = 1L;
        long postId = 12312L;
        String content = "content";
        CreateCommentRequestDTO requestDTO = new CreateCommentRequestDTO(content);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{postId}/comment", postId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void updateComment_Returns201_WhenDataIsValidAndCommentBelongsUser() throws Exception{
        String content = "content";
        String newContent = "new content";
        long userId = 1L;
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setDateCreate(LocalDateTime.now());
        comment.setPostId(postId);
        long commentId = commentRepository.save(comment).getId();
        UpdateCommentRequestDTO requestDTO = new UpdateCommentRequestDTO(newContent);
        MockHttpServletRequestBuilder request= MockMvcRequestBuilders
                 .patch(BASE_URL + "/comment/{commentId}", commentId)
                 .header("X-User-Id", userId)
                 .header("x-User-Role", "USER")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated()
                );
        assertEquals(newContent, commentRepository.findById(commentId).get().getContent());
    }

    @Test
    @Transactional
    void updateComment_Returns409_WhenDataIsValidAndCommentAnotherUser() throws Exception{
        String content = "content";
        String newContent = "new content";
        long userIdAuthorComment = 1L;
        long userId = 2L;
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userIdAuthorComment);
        comment.setDateCreate(LocalDateTime.now());
        comment.setPostId(postId);
        long commentId = commentRepository.save(comment).getId();
        UpdateCommentRequestDTO requestDTO = new UpdateCommentRequestDTO(newContent);
        MockHttpServletRequestBuilder request= MockMvcRequestBuilders
                .patch(BASE_URL + "/comment/{commentId}", commentId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        assertNotEquals(newContent, commentRepository.findById(commentId).get().getContent());
        assertEquals(content, commentRepository.findById(commentId).get().getContent());
    }

    @Test
    @Transactional
    void updateComment_Returns400_WhenDataInvalid() throws Exception{
        String content = "content";
        String newContent = "";
        long userIdAuthorComment = 1L;
        long userId = 1L;
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userIdAuthorComment);
        comment.setDateCreate(LocalDateTime.now());
        comment.setPostId(postId);
        long commentId = commentRepository.save(comment).getId();
        UpdateCommentRequestDTO requestDTO = new UpdateCommentRequestDTO(newContent);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL + "/comment/{commentId}", commentId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        assertNotEquals(newContent, commentRepository.findById(commentId).get().getContent());
        assertEquals(content, commentRepository.findById(commentId).get().getContent());
    }

    @Test
    void updateComment_Returns404_WhenDataIsValidAndNoSuchComment() throws Exception{
        String newContent = "new content";
        long userId = 1L;
        long commentId = 1L;
        UpdateCommentRequestDTO requestDTO = new UpdateCommentRequestDTO(newContent);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL + "/comment/{commentId}", commentId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        assertFalse(commentRepository.existsById(commentId));
    }

    @Test
    @Transactional
    void deleteComment_Returns204_WhenCommentExistsAndCommentBelongsUser() throws  Exception{
        String content = "content";
        long userIdAuthorComment = 1L;
        long userId = 1L;
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userIdAuthorComment);
        comment.setDateCreate(LocalDateTime.now());
        comment.setPostId(postId);
        long commentId = commentRepository.save(comment).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/comment/{commentId}", commentId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertFalse(commentRepository.existsById(commentId));
    }

    @Test
    @Transactional
    void deleteComment_Returns409_WhenCommentExistsAndAnotherUser() throws  Exception{
        String content = "content";
        long userIdAuthorComment = 2L;
        long userId = 1L;
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userIdAuthorComment);
        comment.setDateCreate(LocalDateTime.now());
        comment.setPostId(postId);
        long commentId = commentRepository.save(comment).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/comment/{commentId}", commentId)
                .header("X-User-Id", userId)
                .header("x-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        assertTrue(commentRepository.existsById(commentId));
    }

    @Test
    void deleteComment_Returns404_WhenNoSuchComment() throws  Exception{
        long userId = 1L;
        long commentId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/comment/{commentId}", commentId)
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