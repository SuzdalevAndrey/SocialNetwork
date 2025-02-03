package ru.andreyszdlv.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.andreyszdlv.userservice.dto.controller.UpdateEmailRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.UpdatePasswordRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
class UserProfileControllerIT extends BaseIT {

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

    @MockBean
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepo userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void getUserById_Returns200_WhenUserExists() throws Exception{
        User user = new User();
        String name = "name";
        String email = "email@mail.ru";
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(ERole.USER);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/profile")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");
        UserResponseDTO userResponseDTO = UserResponseDTO
                .builder()
                .name(name)
                .email(email)
                .idImage(null)
                .build();

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(userResponseDTO))
                );
    }

    @Test
    public void getUserById_Returns404_WhenUserNoExists() throws Exception{
        long userId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/profile")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    public void updateEmailUser_Returns201_WhenEmailUpdatedSuccessfully() throws Exception{
        String newEmail = "newemail@example.com";
        String oldEmail = "oldemail@example.com";
        User user = new User();
        user.setName("name");
        user.setEmail(oldEmail);
        user.setPassword("password");
        user.setRole(ERole.USER);
        UpdateEmailRequestDTO requestDTO = new UpdateEmailRequestDTO(newEmail);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/edit-email")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        content().string("Email успешно обновлён")
                );
        assertEquals(newEmail, userRepository.findById(userId).get().getEmail());
    }

    @Test
    public void updateEmailUser_Returns400_WhenEmailInvalid() throws Exception{
        long userId = 1L;
        String newEmail = "";
        UpdateEmailRequestDTO requestDTO = new UpdateEmailRequestDTO(newEmail);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/edit-email")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    public void updateEmailUser_Returns404_WhenUserNoExists() throws Exception{
        long userId = 1L;
        String newEmail = "newemail@example.com";
        UpdateEmailRequestDTO requestDTO = new UpdateEmailRequestDTO(newEmail);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/edit-email")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    public void updatePasswordUser_Returns201_WhenPasswordUpdatedSuccessfully() throws Exception{
        String oldPassword = "000000";
        String newPassword = "0000000";
        String email = "email@mail.ru";
        User user = new User();
        user.setName("name");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(oldPassword));
        user.setRole(ERole.USER);
        UpdatePasswordRequestDTO requestDTO = new UpdatePasswordRequestDTO(oldPassword, newPassword);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/change-password")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        content().string("Пароль успешно изменён")
                );
        assertTrue(passwordEncoder.matches(newPassword, userRepository.findById(userId).get().getPassword()));
    }

    @Test
    public void updatePasswordUser_Returns400_WhenPasswordInvalid() throws Exception{
        long userId = 1L;
        String oldPassword = "";
        String newPassword = "";
        UpdatePasswordRequestDTO requestDTO = new UpdatePasswordRequestDTO(oldPassword, newPassword);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/change-password")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    public void updatePasswordUser_Returns404_WhenUserNoExists() throws Exception{
        long userId = 1L;
        String oldPassword = "000000";
        String newPassword = "0000000";
        UpdatePasswordRequestDTO requestDTO = new UpdatePasswordRequestDTO(oldPassword, newPassword);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/change-password")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    public void updatePasswordUser_Returns409_WhenDifferentPasswords() throws Exception{
        String oldPassword = "0000000";
        String newPassword = "00000000";
        String email = "email@mail.ru";
        User user = new User();
        user.setName("name");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("000000"));
        user.setRole(ERole.USER);
        UpdatePasswordRequestDTO requestDTO = new UpdatePasswordRequestDTO(oldPassword, newPassword);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/change-password")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
        assertFalse(passwordEncoder.matches(newPassword, userRepository.findById(userId).get().getPassword()));
        verify(applicationEventPublisher, never()).publishEvent(new EditPasswordKafkaDTO(email));
    }
}
