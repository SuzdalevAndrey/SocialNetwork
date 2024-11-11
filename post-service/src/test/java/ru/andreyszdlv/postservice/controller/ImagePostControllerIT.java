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
import ru.andreyszdlv.postservice.dto.controller.post.PostImageUrlResponseDTO;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
class ImagePostControllerIT extends BaseIT{

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
    MockMvc mockMvc;

    @Autowired
    PostRepo postRepository;

    @Autowired
    ObjectMapper objectMapper;

    String BASE_URL = "/api/posts";

    @Test
    @Transactional
    void getImagesByPostId_Returns200AndListUrlImage_WhenPostExistsAndPostHasImage()
            throws Exception {
        long userId = 1L;
        List<String> imageIds = List.of("imageId1", "imageId2");
        List<String> urls = List.of("http://localhost:3132/url1", "http://localhost:3132/url2");
        Post post = new Post();
        post.setUserId(userId);
        post.setDateCreate(LocalDateTime.now());
        post.setContent("content");
        post.setImageIds(imageIds);
        long postId = postRepository.save(post).getId();
        for (int i = 0; i < imageIds.size(); i++) {
            when(s3Service.getFileUrlById(imageIds.get(i))).thenReturn(urls.get(i));
        }
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/{postId}/images", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");
        String expectedJson = """
            [
                {"url": "%s"},
                {"url": "%s"}
            ]
            """.formatted(urls.get(0), urls.get(1));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists(),
                        content().json(expectedJson)
                );
    }

    @Test
    @Transactional
    void getImagesByPostId_Returns200AndEmptyListUrlImage_WhenPostExistsAndPostNotHasImage()
            throws Exception {
        long userId = 1L;
        Post post = new Post();
        post.setUserId(userId);
        post.setDateCreate(LocalDateTime.now());
        post.setContent("content");
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/{postId}/images", postId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists(),
                        content().json(
                                        """
                                        []
                                        """
                        )
                );
    }

    @Test
    void getImagesByPostId_Returns404_WhenPostNotExists()
            throws Exception {
        long userId = 1L;
        long postId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/{postId}/images", postId)
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
    void getImageByImageId_Returns200AndImageUrl_WhenImageExists()
            throws Exception {
        long userId = 1L;
        String imageId = "imageId";
        String url = "http://localhost:3132/url1";
        when(s3Service.getFileUrlById(imageId)).thenReturn(url);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/images/{imageId}", imageId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        String responseString = mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists()
                ).andReturn().getResponse().getContentAsString();
        PostImageUrlResponseDTO responseDTO = objectMapper.readValue(
                responseString,
                PostImageUrlResponseDTO.class
        );

        assertEquals(url, responseDTO.url());
    }

    @Test
    void getImageByImageId_Returns404_WhenImageNotExists()
            throws Exception {
        long userId = 1L;
        String imageId = "imageId";
        when(s3Service.getFileUrlById(imageId)).thenThrow(RuntimeException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL + "/images/{imageId}", imageId)
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
    void addImagesPostByPostId_Returns201AndListUrlImage_WhenDataIsValidAndPostExistsAndPostHasImage()
            throws Exception {
        long userId = 1L;
        List<String> imageIds = List.of("imageId1", "imageId2");
        String url = "http://localhost:3132";
        Post post = new Post();
        post.setUserId(userId);
        post.setDateCreate(LocalDateTime.now());
        post.setContent("content");
        post.setImageIds(imageIds);
        long postId = postRepository.save(post).getId();
        when(s3Service.getFileUrlById(anyString()))
                .thenAnswer(invocation -> url + "/" + invocation.getArgument(0));
        MockMultipartFile image = new MockMultipartFile(
                "images",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image content".getBytes()
        );

        mockMvc.perform(
                        multipart(BASE_URL + "/{postId}/images", postId)
                                .file(image)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                )
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists(),
                        jsonPath("[0].url").value(url+"/"+imageIds.get(0)),
                        jsonPath("[1].url").value(url+"/"+imageIds.get(1))
                );

        assertEquals(3, postRepository.findById(postId).get().getImageIds().size());
    }

    @Test
    @Transactional
    void addImagesPostByPostId_Returns201AndListUrlImage_WhenDataIsValidAndPostExistsAndPostNotHasImage()
            throws Exception {
        long userId = 1L;
        String url = "http://localhost:3132";
        Post post = new Post();
        post.setUserId(userId);
        post.setDateCreate(LocalDateTime.now());
        post.setContent("content");
        long postId = postRepository.save(post).getId();
        when(s3Service.getFileUrlById(anyString()))
                .thenAnswer(invocation -> url + "/" + invocation.getArgument(0));
        MockMultipartFile image = new MockMultipartFile(
                "images",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image content".getBytes()
        );

        mockMvc.perform(
                        multipart(BASE_URL + "/{postId}/images", postId)
                                .file(image)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                )
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists()
                );

        assertNotNull(postRepository.findById(postId).get().getImageIds());
        assertEquals(1, postRepository.findById(postId).get().getImageIds().size());
    }

    @Test
    @Transactional
    void addImagesPostByPostId_Returns400_WhenDataInvalid()
            throws Exception {
        long userId = 1L;
        List<String> imageIds = List.of("imageId1", "imageId2");
        String url = "http://localhost:3132";
        Post post = new Post();
        post.setUserId(userId);
        post.setDateCreate(LocalDateTime.now());
        post.setContent("content");
        post.setImageIds(imageIds);
        long postId = postRepository.save(post).getId();
        when(s3Service.getFileUrlById(anyString()))
                .thenAnswer(invocation -> url + "/" + invocation.getArgument(0));

        mockMvc.perform(
                        multipart(BASE_URL + "/{postId}/images", postId)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );

        assertEquals(2, postRepository.findById(postId).get().getImageIds().size());
    }

    @Test
    void addImagesPostByPostId_Returns404_WhenDataIsValidAndPostNotExists()
            throws Exception {
        long userId = 1L;
        long postId = 2L;
        MockMultipartFile image = new MockMultipartFile(
                "images",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Image content".getBytes()
        );

        mockMvc.perform(
                        multipart(BASE_URL + "/{postId}/images", postId)
                                .file(image)
                                .header("X-User-Id", userId)
                                .header("X-User-Role", "USER")
                )
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void deleteImagePostByPostIdAndImageId_Returns204_WhenPostExistAndPostHasImage()
            throws Exception {
        long userId = 1L;
        String deleteImageId = "imageId1";
        List<String> imageIds = List.of(deleteImageId, "imageId2");
        Post post = new Post();
        post.setUserId(userId);
        post.setDateCreate(LocalDateTime.now());
        post.setContent("content");
        post.setImageIds(imageIds);
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(
                        BASE_URL + "/{postId}/images/{imageId}",
                        postId,
                        deleteImageId
                )
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertEquals(1, postRepository.findById(postId).get().getImageIds().size());
        assertEquals("imageId2", postRepository.findById(postId).get().getImageIds().get(0));
    }

    @Test
    @Transactional
    void deleteImagePostByPostIdAndImageId_Returns404_WhenPostExistAndPostNotHasImage()
            throws Exception {
        long userId = 1L;
        String deleteImageId = "imageId1";
        List<String> imageIds = List.of("imageId2");
        Post post = new Post();
        post.setUserId(userId);
        post.setDateCreate(LocalDateTime.now());
        post.setContent("content");
        post.setImageIds(imageIds);
        long postId = postRepository.save(post).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(
                        BASE_URL + "/{postId}/images/{imageId}",
                        postId,
                        deleteImageId
                )
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );

        assertEquals(1, postRepository.findById(postId).get().getImageIds().size());
        assertEquals("imageId2", postRepository.findById(postId).get().getImageIds().get(0));
    }

    @Test
    void deleteImagePostByPostIdAndImageId_Returns404_WhenNotPostExist()
            throws Exception {
        long userId = 1L;
        String deleteImageId = "imageId1";
        long postId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(
                        BASE_URL + "/{postId}/images/{imageId}",
                        postId,
                        deleteImageId
                )
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }
}