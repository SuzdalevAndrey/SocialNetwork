package ru.andreyszdlv.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
class InternalUserControllerIT extends BaseIT {

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
    UserRepo userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Transactional
    public void getUserEmailByUserId_Return200_WhenUserExists() throws Exception{
        String email = "email@mail.ru";
        User user = new User();
        user.setName("name");
        user.setEmail(email);
        user.setPassword("Password");
        user.setRole(ERole.USER);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{userId}/email", userId);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().string(email)
                );
    }

    @Test
    public void getUserEmailByUserId_Return404_WhenUserNoExists() throws Exception{
        long userId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{userId}/email", userId);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    public void getUserDetailsByUserEmail_Return200_WhenUserExists() throws Exception{
        String email = "email@mail.ru";
        String name = "name";
        String password = "000000";
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(ERole.USER);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{email}/user-details", email);
        UserDetailsResponseDTO responseDTO = UserDetailsResponseDTO
                .builder()
                .id(userId)
                .email(email)
                .name(name)
                .password(password)
                .role(ERole.USER)
                .build();

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().json(
                              objectMapper.writeValueAsString(responseDTO)
                        )
                );
    }

    @Test
    public void getUserDetailsByUserEmail_Return404_WhenUserNoExists() throws Exception{
        String email = "email@mail.ru";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{email}/user-details", email);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    public void existsUserByEmail_ReturnStatus200BodyTrue_WhenUserExists() throws Exception{
        String email = "email@mail.ru";
        User user = new User();
        user.setName("name");
        user.setEmail(email);
        user.setPassword("Password");
        user.setRole(ERole.USER);
        userRepository.save(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{email}/exists", email);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().string("true")
                );
    }

    @Test
    public void existsUserByEmail_ReturnStatus200BodyFalse_WhenUserNoExists() throws Exception{
        String email = "email@mail.ru";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{email}/exists", email);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().string("false")
                );
    }
}
