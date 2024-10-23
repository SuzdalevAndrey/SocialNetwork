package ru.andreyszdlv.userservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.andreyszdlv.userservice.dto.controller.FriendResponseDTO;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FriendControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepo userRepository;

    @MockBean
    TempFriendRepo tempFriendRepository;

    @MockBean
    FriendRepo friendRepository;

    @Test
    void getFriends_ReturnedListFriends_WhenFriendsExists() throws Exception{
        long userId = 1L;
        List<FriendResponseDTO> friends = List.of(
                new FriendResponseDTO("name1", "email1"),
                new FriendResponseDTO("name2", "email2")
        );
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/friends")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(userRepository.findUserFriends(userId)).thenReturn(friends);

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json(
                          """
                          [
                              {
                                "name": "name1",
                                "email":"email1"
                              },
                              {
                                 "name": "name2",
                                 "email":"email2"
                              }
                          ]
                          """
                )
        );
    }

    @Test
    void getFriends_ReturnedEmptyListFriends_WhenFriendsNoExist() throws Exception{
        long userId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/friends")
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(userRepository.findUserFriends(userId)).thenReturn(List.of());
        mockMvc.perform(request)
                .andExpectAll(
                status().isOk(),
                content().json("[]")
        );
    }

    @Test
    void createRequestFriend_Returned201_WhenRequestCreate() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId)).thenReturn(false);
        mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().string("Ваша заявка успешно отправлена")
        );
    }

    @Test
    public void createRequestFriend_Returns409_WhenUsersAlreadyFriends() throws Exception {
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    public void createRequestFriend_Returns409_WhenRequestAlreadySend() throws Exception {
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    public void createRequestFriend_Returns409_WhenRequestMeAlreadySend() throws Exception {
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId)).thenReturn(true);
        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    public void confirmRequestFriend_Returns201_WhenRequestExist() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/confirm-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");

        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().string("Заявка в друзья успешно одобрена")
        );
    }

    @Test
    public void confirmRequestFriend_Returns404_WhenRequestNotFound() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/confirm-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");

        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    public void deleteMyRequest_Returns204_WhenRequestExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends//delete-my-request//{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );
    }

    @Test
    public void deleteMyRequest_Returns404_WhenRequestNoExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-my-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    public void deleteRequest_Returns204_WhenRequestExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");

        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );
    }

    @Test
    public void deleteRequest_Returns404_WhenRequestNoExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");

        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    public void deleteFriend_Returns204_WhenFriendExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-friend/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );
    }

    @Test
    public void deleteFriend_Returns404_WhenFriendNoExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-friend/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }
}
