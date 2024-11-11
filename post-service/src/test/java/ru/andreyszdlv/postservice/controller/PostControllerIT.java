package ru.andreyszdlv.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@AutoConfigureMockMvc
class PostControllerIT extends BaseIT{

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Container
    static GenericContainer<?> redisContainer =
            new GenericContainer<>(DockerImageName.parse("redis:latest"))
                    .withExposedPorts(6379)
                    .waitingFor(Wait.forListeningPort());


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PostRepo postRepository;

    @Autowired
    ObjectMapper objectMapper;

    String BASE_URL = "/api/posts";

    @Test
    @Transactional
    void createPost_Returns201_WhenDataIsValidAndPostHasImage() throws Exception{
        long userId = 1L;
        String content = "content";
        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "image1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image content 1".getBytes()
        );
        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "image2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image content 2".getBytes()
        );

        String response = mockMvc
                .perform(
                        multipart(BASE_URL)
                                .file(image1)
                                .file(image2)
                                .param("content", content)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content").value(content),
                        jsonPath("$.id", notNullValue()),
                        jsonPath("$.imageIds", notNullValue())
                ).andReturn().getResponse().getContentAsString();

        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
        assertTrue(postRepository.existsById(responseDTO.id()));
        assertEquals(userId, postRepository.findById(responseDTO.id()).get().getUserId());
        assertEquals(content, postRepository.findById(responseDTO.id()).get().getContent());
        assertEquals(responseDTO.imageIds(), postRepository.findById(responseDTO.id()).get().getImageIds());
    }

    @Test
    @Transactional
    void createPost_Returns201_WhenDataIsValidAndPostNoImage() throws Exception{
        long userId = 1L;
        String content = "content";

        String response = mockMvc
                .perform(
                        multipart(BASE_URL)
                                .param("content", content)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content").value(content),
                        jsonPath("$.id", notNullValue())
                ).andReturn()
                .getResponse()
                .getContentAsString();

        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
        assertTrue(postRepository.existsById(responseDTO.id()));
        assertEquals(userId, postRepository.findById(responseDTO.id()).get().getUserId());
        assertEquals(content, postRepository.findById(responseDTO.id()).get().getContent());
        assertEquals(List.of(), responseDTO.imageIds());
        assertEquals(List.of(), postRepository.findById(responseDTO.id()).get().getImageIds());
    }


    @Test
    void createPost_Returns400_WhenDataInvalid() throws Exception{
        long userId = 1L;
        String content = "";
        int countPost = postRepository.findAll().size();

       mockMvc.perform(multipart(BASE_URL)
                       .param("content", content)
                       .header("X-User-Id", userId)
                       .header("X-User-Role", "USER")
                       .contentType(MediaType.MULTIPART_FORM_DATA)
               )
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );

       assertEquals(countPost, postRepository.findAll().size());
    }

    @Test
    @Transactional
    void updatePost_Returns201_WhenDataIsValidAndPostExistsAndThisUserCreatePostAndUpdatePostHasImage()
            throws Exception{
        long userId = 1L;
        String oldContent = "oldContent";
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(oldContent);
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();
        String newContent = "newContent";
        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "image1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image content 1".getBytes()
        );
        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "image2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image content 2".getBytes()
        );


        String response = mockMvc.perform(
                        multipart(BASE_URL+"/{postId}", postId)
                                .file(image1)
                                .file(image2)
                                .param("content", newContent)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        })
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
        assertEquals(newContent, postRepository.findById(postId).get().getContent());
        assertEquals(responseDTO.imageIds(), postRepository.findById(postId).get().getImageIds());
        assertNotNull(responseDTO.imageIds());
        assertNotNull(postRepository.findById(postId).get().getImageIds());
    }

    @Test
    @Transactional
    void updatePost_Returns201_WhenDataIsValidAndPostExistsAndThisUserCreatePostAndUpdatePostNotHasImage()
            throws Exception{
        long userId = 1L;
        String oldContent = "oldContent";
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(oldContent);
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();
        String newContent = "newContent";

        mockMvc.perform(
                        multipart(BASE_URL+"/{postId}", postId)
                                .param("content", newContent)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        })
                )
                .andExpect(status().isCreated());

        assertEquals(newContent, postRepository.findById(postId).get().getContent());
        assertEquals(List.of(), postRepository.findById(postId).get().getImageIds());
    }

    @Test
    void updatePost_Returns404_WhenDataIsValidAndPostNoSuch()
            throws Exception{
        long userId = 1L;
        long postId = 2L;
        String newContent = "newContent";

        mockMvc.perform(
                        multipart(BASE_URL + "/{postId}", postId)
                                .param("content", newContent)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                                .with(
                                        request -> {
                                            request.setMethod("PATCH");
                                            return request;
                                        })
                )
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void updatePost_Returns409_WhenDataIsValidAndPostExistsAndThisUserNoCreatePost()
            throws Exception{
        long userId = 1L;
        long authorPostId = 2L;
        String oldContent = "oldContent";
        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent(oldContent);
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();
        String newContent = "newContent";

        mockMvc.perform(
                        multipart(BASE_URL+"/{postId}",postId)
                                .param("content", newContent)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                                .with(
                                        request ->{
                                            request.setMethod("PATCH");
                                            return request;
                                        })
                )
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );

        assertNotEquals(newContent, postRepository.findById(postId).get().getContent());
        assertEquals(oldContent, postRepository.findById(postId).get().getContent());
    }

    @Test
    @Transactional
    void updatePost_Returns400_WhenDataInvalid()
            throws Exception{
        long userId = 1L;
        String oldContent = "oldContent";
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(oldContent);
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();
        String newContent = "";

        mockMvc.perform(
                        multipart(BASE_URL+"/{postId}", postId)
                                .param("content", newContent)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                                .with(
                                        request->{
                                            request.setMethod("PATCH");
                                            return request;
                                        })
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );

        assertNotEquals(newContent, postRepository.findById(postId).get().getContent());
        assertEquals(oldContent, postRepository.findById(postId).get().getContent());
    }

    @Test
    @Transactional
    void deletePost_Returns204_WhenPostExistsAndThisUserCreatePost() throws Exception{
        long userId = 1L;
        Post post = new Post();
        post.setUserId(userId);
        post.setContent("content");
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/{postId}", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertFalse(postRepository.existsById(postId));
    }

    @Test
    void deletePost_Returns404_WhenPostNoSuch() throws Exception{
        long userId = 1L;
        long postId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/{postId}", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void deletePost_Returns409_WhenPostExistsAndThisUserNoCreatePost()
            throws Exception{
        long userId = 1L;
        long authorPostId = 2L;
        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent("content");
        post.setDateCreate(LocalDateTime.now());
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/{postId}", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );

        assertTrue(postRepository.existsById(postId));
    }

    @Test
    @Transactional
    void getPostById_ReturnsPostAndStatus200_WhenPostExistsAndUserCreatePost() throws Exception{
        long userId = 1L;
        String content = "content";
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/{postId}", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        String response = mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists()
                ).andReturn()
                .getResponse()
                .getContentAsString();

        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
        assertEquals(content, responseDTO.content());
        assertEquals(userId, responseDTO.userId());
        assertEquals(1, responseDTO.numberViews());
    }

    @Test
    @Transactional
    void getPostById_ReturnsPostAndStatus200_WhenPostExistsAndUserNoCreatePost() throws Exception{
        long userId = 1L;
        long authorPostId = 2L;
        String content = "content";
        Post post = new Post();
        post.setUserId(authorPostId);
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/{postId}", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        String response = mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists()
                ).andReturn()
                .getResponse()
                .getContentAsString();

        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
        assertEquals(content, responseDTO.content());
        assertEquals(authorPostId, responseDTO.userId());
        assertEquals(1, responseDTO.numberViews());
    }

    @Test
    void getPostById_ReturnsStatus404_WhenPostNoSuch() throws Exception{
        long userId = 1L;
        long postId = 312L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/{postId}", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void getPostsByUserId_ReturnsListPostAndStatus200_WhenPostsExists() throws Exception{
        long userId = 1L;
        String content1 = "content1";
        LocalDateTime timeCreatePost1 = LocalDateTime.now();
        Post post1 = new Post();
        post1.setUserId(userId);
        post1.setContent(content1);
        post1.setDateCreate(timeCreatePost1);
        post1.setNumberViews(0L);
        long postId1 = postRepository.save(post1).getId();
        String content2 = "content2";
        LocalDateTime timeCreatePost2 = LocalDateTime.now();
        Post post2 = new Post();
        post2.setUserId(userId);
        post2.setContent(content2);
        post2.setDateCreate(LocalDateTime.now());
        post2.setNumberViews(0L);
        long postId2 = postRepository.save(post2).getId();
        List<PostResponseDTO> expectedPosts = List.of(
                new PostResponseDTO(postId1, content1,0L, timeCreatePost1, userId, null, null, List.of()),
                new PostResponseDTO(postId2, content2,0L, timeCreatePost2, userId, null, null, List.of())
        );
        String expectedJson = objectMapper.writeValueAsString(expectedPosts);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists(),
                        content().json(expectedJson)
                );
    }

    @Test
    void getPostsByUserId_ReturnsEmptyListPostAndStatus200_WhenPostsNoSuch() throws Exception{
        long userId = 1L;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists(),
                        content().json("""
                                        []
                                        """)
                );
    }
}