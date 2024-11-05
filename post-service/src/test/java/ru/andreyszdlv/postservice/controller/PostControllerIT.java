//package ru.andreyszdlv.postservice.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import ru.andreyszdlv.postservice.configuration.KafkaProducerConfig;
//import ru.andreyszdlv.postservice.dto.controller.post.CreatePostRequestDTO;
//import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
//import ru.andreyszdlv.postservice.dto.controller.post.UpdatePostRequestDTO;
//import ru.andreyszdlv.postservice.model.Post;
//import ru.andreyszdlv.postservice.repository.PostRepo;
//import ru.andreyszdlv.postservice.service.KafkaProducerService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.hamcrest.Matchers.notNullValue;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@Testcontainers
//@AutoConfigureMockMvc
//class PostControllerIT {
//
//    @Container
//    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
//    @Autowired
//    private MockMvc mockMvc;
//
//    @DynamicPropertySource
//    static void dynamicProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//    }
//
//    @Autowired
//    PostRepo postRepository;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @MockBean
//    KafkaProducerService kafkaProducerService;
//
//    @MockBean
//    KafkaProducerConfig kafkaProducerConfig;
//
//    String BASE_URL = "/api/posts";
//
//    @Test
//    @Transactional
//    void createPost_Returns201_WhenDataIsValid() throws Exception{
//        long userId = 1L;
//        String content = "content";
//        CreatePostRequestDTO requestDTO = new CreatePostRequestDTO(content);
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .post(BASE_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestDTO))
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        String response = mockMvc.perform(request)
//                .andExpectAll(
//                        status().isCreated(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$.content").value(content),
//                        jsonPath("$.id", notNullValue())
//                ).andReturn()
//                .getResponse()
//                .getContentAsString();
//        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
//        assertTrue(postRepository.existsById(responseDTO.id()));
//        assertEquals(userId, postRepository.findById(responseDTO.id()).get().getUserId());
//        assertEquals(content, postRepository.findById(responseDTO.id()).get().getContent());
//    }
//
//    @Test
//    void createPost_Returns400_WhenDataInvalid() throws Exception{
//        long userId = 1L;
//        String content = "";
//        int countPost = postRepository.findAll().size();
//        CreatePostRequestDTO requestDTO = new CreatePostRequestDTO(content);
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .post(BASE_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestDTO))
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//       mockMvc.perform(request)
//                .andExpectAll(
//                        status().isBadRequest(),
//                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
//                        jsonPath("$").exists()
//                );
//       assertEquals(countPost, postRepository.findAll().size());
//    }
//
//    @Test
//    @Transactional
//    void updatePost_Returns201_WhenDataIsValidAndPostExistsAndThisUserCreatePost()
//            throws Exception{
//        long userId = 1L;
//        String oldContent = "oldContent";
//        Post post = new Post();
//        post.setUserId(userId);
//        post.setContent(oldContent);
//        post.setDateCreate(LocalDateTime.now());
//        long postId = postRepository.save(post).getId();
//        String newContent = "newContent";
//        UpdatePostRequestDTO requestDTO = new UpdatePostRequestDTO(newContent);
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .patch(BASE_URL + "/{postId}", postId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestDTO))
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpect(status().isCreated());
//        assertEquals(newContent, postRepository.findById(postId).get().getContent());
//    }
//
//    @Test
//    void updatePost_Returns404_WhenDataIsValidAndPostNoSuch()
//            throws Exception{
//        long userId = 1L;
//        long postId = 2L;
//        String newContent = "newContent";
//        UpdatePostRequestDTO requestDTO = new UpdatePostRequestDTO(newContent);
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .patch(BASE_URL + "/{postId}", postId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestDTO))
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isNotFound(),
//                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
//                        jsonPath("$").exists()
//                );
//    }
//
//    @Test
//    @Transactional
//    void updatePost_Returns409_WhenDataIsValidAndPostExistsAndThisUserNoCreatePost()
//            throws Exception{
//        long userId = 1L;
//        long authorPostId = 2L;
//        String oldContent = "oldContent";
//        Post post = new Post();
//        post.setUserId(authorPostId);
//        post.setContent(oldContent);
//        post.setDateCreate(LocalDateTime.now());
//        long postId = postRepository.save(post).getId();
//        String newContent = "newContent";
//        UpdatePostRequestDTO requestDTO = new UpdatePostRequestDTO(newContent);
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .patch(BASE_URL + "/{postId}", postId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestDTO))
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isConflict(),
//                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
//                        jsonPath("$").exists()
//                );
//        assertNotEquals(newContent, postRepository.findById(postId).get().getContent());
//        assertEquals(oldContent, postRepository.findById(postId).get().getContent());
//    }
//
//    @Test
//    @Transactional
//    void updatePost_Returns400_WhenDataInvalid()
//            throws Exception{
//        long userId = 1L;
//        String oldContent = "oldContent";
//        Post post = new Post();
//        post.setUserId(userId);
//        post.setContent(oldContent);
//        post.setDateCreate(LocalDateTime.now());
//        long postId = postRepository.save(post).getId();
//        String newContent = "";
//        UpdatePostRequestDTO requestDTO = new UpdatePostRequestDTO(newContent);
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .patch(BASE_URL + "/{postId}", postId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestDTO))
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isBadRequest(),
//                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
//                        jsonPath("$").exists()
//                );
//        assertNotEquals(newContent, postRepository.findById(postId).get().getContent());
//        assertEquals(oldContent, postRepository.findById(postId).get().getContent());
//    }
//
//    @Test
//    @Transactional
//    void deletePost_Returns204_WhenPostExistsAndThisUserCreatePost() throws Exception{
//        long userId = 1L;
//        Post post = new Post();
//        post.setUserId(userId);
//        post.setContent("content");
//        post.setDateCreate(LocalDateTime.now());
//        long postId = postRepository.save(post).getId();
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .delete(BASE_URL + "/{postId}", postId)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpect(status().isNoContent());
//        assertFalse(postRepository.existsById(postId));
//    }
//
//    @Test
//    void deletePost_Returns404_WhenPostNoSuch() throws Exception{
//        long userId = 1L;
//        long postId = 2L;
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .delete(BASE_URL + "/{postId}", postId)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isNotFound(),
//                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
//                        jsonPath("$").exists()
//                );
//    }
//
//    @Test
//    @Transactional
//    void deletePost_Returns409_WhenPostExistsAndThisUserNoCreatePost()
//            throws Exception{
//        long userId = 1L;
//        long authorPostId = 2L;
//        Post post = new Post();
//        post.setUserId(authorPostId);
//        post.setContent("content");
//        post.setDateCreate(LocalDateTime.now());
//        long postId = postRepository.save(post).getId();
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .delete(BASE_URL + "/{postId}", postId)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isConflict(),
//                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
//                        jsonPath("$").exists()
//                );
//        assertTrue(postRepository.existsById(postId));
//    }
//
//    @Test
//    @Transactional
//    void getPostById_ReturnsPostAndStatus200_WhenPostExistsAndUserCreatePost() throws Exception{
//        long userId = 1L;
//        String content = "content";
//        Post post = new Post();
//        post.setUserId(userId);
//        post.setContent(content);
//        post.setDateCreate(LocalDateTime.now());
//        post.setNumberViews(0L);
//        long postId = postRepository.save(post).getId();
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .get(BASE_URL + "/{postId}", postId)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        String response = mockMvc.perform(request)
//                .andExpectAll(
//                        status().isOk(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$").exists()
//                ).andReturn()
//                .getResponse()
//                .getContentAsString();
//        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
//        assertEquals(content, responseDTO.content());
//        assertEquals(userId, responseDTO.userId());
//        assertEquals(1, responseDTO.numberViews());
//    }
//
//    @Test
//    @Transactional
//    void getPostById_ReturnsPostAndStatus200_WhenPostExistsAndUserNoCreatePost() throws Exception{
//        long userId = 1L;
//        long authorPostId = 2L;
//        String content = "content";
//        Post post = new Post();
//        post.setUserId(authorPostId);
//        post.setContent(content);
//        post.setDateCreate(LocalDateTime.now());
//        post.setNumberViews(0L);
//        long postId = postRepository.save(post).getId();
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .get(BASE_URL + "/{postId}", postId)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        String response = mockMvc.perform(request)
//                .andExpectAll(
//                        status().isOk(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$").exists()
//                ).andReturn()
//                .getResponse()
//                .getContentAsString();
//        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
//        assertEquals(content, responseDTO.content());
//        assertEquals(authorPostId, responseDTO.userId());
//        assertEquals(1, responseDTO.numberViews());
//    }
//
//    @Test
//    void getPostById_ReturnsStatus404_WhenPostNoSuch() throws Exception{
//        long userId = 1L;
//        long postId = 312L;
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .get(BASE_URL + "/{postId}", postId)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isNotFound(),
//                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
//                        jsonPath("$").exists()
//                );
//    }
//
//    @Test
//    @Transactional
//    void getPostsByUserId_ReturnsListPostAndStatus200_WhenPostsExists() throws Exception{
//        long userId = 1L;
//
//        String content1 = "content1";
//        LocalDateTime timeCreatePost1 = LocalDateTime.now();
//        Post post1 = new Post();
//        post1.setUserId(userId);
//        post1.setContent(content1);
//        post1.setDateCreate(timeCreatePost1);
//        post1.setNumberViews(0L);
//        long postId1 = postRepository.save(post1).getId();
//
//        String content2 = "content2";
//        LocalDateTime timeCreatePost2 = LocalDateTime.now();
//        Post post2 = new Post();
//        post2.setUserId(userId);
//        post2.setContent(content2);
//        post2.setDateCreate(LocalDateTime.now());
//        post2.setNumberViews(0L);
//        long postId2 = postRepository.save(post2).getId();
//
//        List<PostResponseDTO> expectedPosts = List.of(
//                new PostResponseDTO(postId1, content1,0L, timeCreatePost1, userId, null, null),
//                new PostResponseDTO(postId2, content2,0L, timeCreatePost2, userId, null, null)
//        );
//
//        String expectedJson = objectMapper.writeValueAsString(expectedPosts);
//
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .get(BASE_URL)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isOk(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$").exists(),
//                        content().json(expectedJson)
//                );
//    }
//
//    @Test
//    void getPostsByUserId_ReturnsEmptyListPostAndStatus200_WhenPostsNoSuch() throws Exception{
//        long userId = 1L;
//
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//                .get(BASE_URL)
//                .header("X-User-Id", userId)
//                .header("X-User-Role", "USER");
//
//        mockMvc.perform(request)
//                .andExpectAll(
//                        status().isOk(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$").exists(),
//                        content().json("""
//                                        []
//                                        """)
//                );
//    }
//}