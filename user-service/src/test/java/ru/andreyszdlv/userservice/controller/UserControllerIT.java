package ru.andreyszdlv.userservice.controller;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepo userRepository;

    @Test
    public void updateEmailUser_Returns201_WhenEmailUpdatedSuccessfully() throws Exception{
        long userId = 1L;
        String newEmail = "newemail@example.com";
        User user = new User();
        user.setId(userId);
        user.setEmail("oldemail@example.com");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/edit-email")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + newEmail + "\"}");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        content().string("Email успешно обновлён")
                );
    }

    @Test
    public void updateEmailUser_Returns400_WhenEmailInvalid() throws Exception{
        long userId = 1L;
        String newEmail = "";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/edit-email")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + newEmail + "\"}");

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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch("/api/user/edit-email")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + newEmail + "\"}");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

}
