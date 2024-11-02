package ru.andreyszdlv.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.andreyszdlv.userservice.configuration.KafkaConsumerConfig;
import ru.andreyszdlv.userservice.configuration.KafkaProducerConfig;
import ru.andreyszdlv.userservice.dto.controller.InternalUserResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.listener.SaveUserEventListener;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import ru.andreyszdlv.userservice.service.KafkaProducerService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class InternalUserControllerIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UserRepo userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    KafkaProducerService kafkaProducerService;

    @MockBean
    SaveUserEventListener saveUserEventListener;

    @MockBean
    KafkaConsumerConfig kafkaConsumerConfig;

    @MockBean
    KafkaProducerConfig kafkaProducerConfig;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    @Transactional
    public void getUserEmailByUserId_Return200_WhenUserExists() throws Exception{
        String email = "email@mail.ru";
        User user = new User();
        user.setEmail(email);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/email/{userId}", userId);

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
                .get("/internal/user/email/{userId}", userId);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    public void getNameByUserId_Return200_WhenUserExists() throws Exception{
        String name = "name";
        User user = new User();
        user.setName(name);
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/name/{userId}", userId);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().string(name)
                );
    }

    @Test
    public void getNameByUserId_Return404_WhenUserNoExists() throws Exception{
        long userId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/name/{userId}", userId);

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
        long userId = userRepository.save(user).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/user-details/{email}", email);
        UserDetailsResponseDTO responseDTO = UserDetailsResponseDTO
                .builder()
                .id(userId)
                .email(email)
                .name(name)
                .password(password)
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
                .get("/internal/user/user-details/{email}", email);

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
        user.setEmail(email);
        userRepository.save(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/exists/{email}", email);

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
                .get("/internal/user/exists/{email}", email);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().string("false")
                );
    }

    @Test
    @Transactional
    public void getUserByEmail_Return200_WhenUserExists() throws Exception{
        String email = "email@mail.ru";
        String name = "name";
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        long userId = userRepository.save(user).getId();
        InternalUserResponseDTO responseDTO = InternalUserResponseDTO
                .builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{email}", email);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().json(
                                objectMapper.writeValueAsString(responseDTO)
                        )
                );
    }

    @Test
    public void getUserByEmail_Return404_WhenUserNoExists() throws Exception{
        String email = "email@mail.ru";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/internal/user/{email}", email);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }
}
