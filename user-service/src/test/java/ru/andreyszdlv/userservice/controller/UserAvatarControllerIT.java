package ru.andreyszdlv.userservice.controller;

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
import ru.andreyszdlv.userservice.dto.controller.ImageIdResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageUrlResponseDTO;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@Testcontainers
class UserAvatarControllerIT extends BaseIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest");

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
    UserRepo userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Transactional
    public void uploadAvatar_Return201_WhenUploadAvatarSuccessfully() throws Exception {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        long userId = userRepository.save(user).getId();
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String responseString = mockMvc.perform(
                multipart("/api/user/my-avatar")
                        .file(image)
                        .header("X-User-Id", userId)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").exists()
        ).andReturn().getResponse().getContentAsString();
        ImageIdResponseDTO responseDTO = objectMapper.readValue(
                responseString,
                ImageIdResponseDTO.class
        );

        verify(s3Service, times(1)).saveFile(any(),any());
        assertEquals(responseDTO.imageId(), userRepository.findById(userId).get().getIdImage());
    }

    @Test
    public void uploadAvatar_Return400_WhenDataInvalid() throws Exception {
        long userId = 1L;

        mockMvc.perform(
                multipart("/api/user/my-avatar")
                        .header("X-User-Id", userId)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    public void uploadAvatar_Return409_WhenUserAlreadyHasAvatar() throws Exception {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        user.setIdImage("imageId");
        long userId = userRepository.save(user).getId();
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(
                multipart("/api/user/my-avatar")
                        .file(image)
                        .header("X-User-Id", userId)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        verify(s3Service, never()).saveFile(any(),any());
    }

    @Test
    @Transactional
    public void updateAvatar_Return201_WhenUploadAvatarSuccessfully() throws Exception {
        User user = new User();
        String imageId = "imageId";
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        user.setIdImage(imageId);
        long userId = userRepository.save(user).getId();
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String responseString = mockMvc.perform(
                multipart("/api/user/my-avatar")
                        .file(image)
                        .header("X-User-Id", userId)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(
                                request ->
                                {
                                    request.setMethod("PATCH");
                                    return request;
                                }
                        )
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$").exists()
        ).andReturn().getResponse().getContentAsString();
        ImageIdResponseDTO responseDTO = objectMapper.readValue(
                responseString,
                ImageIdResponseDTO.class
        );

        verify(s3Service, times(1)).saveFile(any(),any());
        assertEquals(responseDTO.imageId(), userRepository.findById(userId).get().getIdImage());
        assertNotEquals(imageId, userRepository.findById(userId).get().getIdImage());
    }

    @Test
    public void updateAvatar_Return400_WhenDataInvalid() throws Exception {
        long userId = 1L;

        mockMvc.perform(
                multipart("/api/user/my-avatar")
                        .header("X-User-Id", userId)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(
                                request ->
                                {
                                    request.setMethod("PATCH");
                                    return request;
                                }
                        )
        ).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    public void updateAvatar_Return409_WhenUserNotHasAvatar() throws Exception {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        long userId = userRepository.save(user).getId();
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(
                multipart("/api/user/my-avatar")
                        .file(image)
                        .header("X-User-Id", userId)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(
                                request ->
                                {
                                    request.setMethod("PATCH");
                                    return request;
                                }
                        )
        ).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        verify(s3Service, never()).saveFile(any(),any());
    }

    @Test
    @Transactional
    public void getAvatarByUserId_Return200_WhenAvatarExists() throws Exception{
        User user = new User();
        String imageId = "imageId";
        String exceptedUrl = "http://localhost:2132/image/" + imageId;
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        user.setIdImage(imageId);
        long userId = userRepository.save(user).getId();
        when(s3Service.getFileUrlById(imageId)).thenReturn(exceptedUrl);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/my-avatar")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        String responseString = mockMvc
                .perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists()
                ).andReturn().getResponse().getContentAsString();

        ImageUrlResponseDTO responseDTO = objectMapper.readValue(
                responseString,
                ImageUrlResponseDTO.class
        );

        assertEquals(exceptedUrl, responseDTO.url());
    }

    @Test
    @Transactional
    public void getAvatarByUserId_Return404_WhenAvatarNotExists() throws Exception{
        User user = new User();
        String imageId = "imageId";
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        user.setIdImage(imageId);
        long userId = userRepository.save(user).getId();
        when(s3Service.getFileUrlById(imageId)).thenThrow(RuntimeException.class);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/my-avatar")
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
    public void getAvatarByIdImage_Return200_WhenAvatarExists() throws Exception{
        long userId = 1L;
        String imageId = "imageId";
        String exceptedUrl = "http://localhost:2132/image/" + imageId;
        when(s3Service.getFileUrlById(imageId)).thenReturn(exceptedUrl);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/avatar/{imageId}", imageId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        String responseString = mockMvc
                .perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").exists()
                ).andReturn().getResponse().getContentAsString();

        ImageUrlResponseDTO responseDTO = objectMapper.readValue(
                responseString,
                ImageUrlResponseDTO.class
        );

        assertEquals(exceptedUrl, responseDTO.url());
    }

    @Test
    public void getAvatarByIdImage_Return404_WhenAvatarNotExists() throws Exception{
        long userId = 1L;
        String imageId = "imageId";
        when(s3Service.getFileUrlById(imageId)).thenThrow(RuntimeException.class);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/avatar/{imageId}", imageId)
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
    public void deleteAvatarByUserId_Return204_WhenUserHasExists() throws Exception{
        User user = new User();
        String imageId = "imageId";
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        user.setIdImage(imageId);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/my-avatar")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertEquals(null, userRepository.findById(userId).get().getIdImage());
        verify(s3Service, times(1)).deleteFileById(imageId);
    }

    @Test
    @Transactional
    public void deleteAvatarByUserId_Return409_WhenUserNotHasAvatar() throws Exception{
        User user = new User();
        String imageId = "imageId";
        user.setName("name");
        user.setEmail("email@mail.ru");
        user.setPassword("password");
        user.setRole(ERole.USER);
        long userId = userRepository.save(user).getId();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/my-avatar")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }
}