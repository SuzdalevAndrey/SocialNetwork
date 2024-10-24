package ru.andreyszdlv.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.andreyszdlv.userservice.configuration.KafkaConsumerConfig;
import ru.andreyszdlv.userservice.configuration.KafkaProducerConfig;
import ru.andreyszdlv.userservice.dto.controller.UpdateEmailRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.UpdatePasswordRequestDTO;
import ru.andreyszdlv.userservice.listener.SaveUserEventListener;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.andreyszdlv.userservice.service.KafkaProducerService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepo userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    KafkaProducerService kafkaProducerService;

    @MockBean
    SaveUserEventListener saveUserEventListener;

    @MockBean
    KafkaConsumerConfig kafkaConsumerConfig;

    @MockBean
    KafkaProducerConfig kafkaProducerConfig;

    @Test
    @Transactional
    public void updateEmailUser_Returns201_WhenEmailUpdatedSuccessfully() throws Exception{
        String newEmail = "newemail@example.com";
        String oldEmail = "oldemail@example.com";
        User user = new User();
        user.setEmail(oldEmail);
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
        verify(kafkaProducerService, times(1)).sendEditEmailEvent(oldEmail, newEmail);
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
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("000000"));
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
        verify(kafkaProducerService, times(1)).sendEditPasswordEvent(email);
    }
}
