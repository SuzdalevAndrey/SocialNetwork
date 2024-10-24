package ru.andreyszdlv.userservice.controller;

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
import ru.andreyszdlv.userservice.listener.SaveUserEventListener;
import ru.andreyszdlv.userservice.model.Friend;
import ru.andreyszdlv.userservice.model.TempFriend;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;
import ru.andreyszdlv.userservice.repository.UserRepo;
import ru.andreyszdlv.userservice.service.KafkaProducerService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest()
@AutoConfigureMockMvc
class FriendControllerIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepo userRepository;

    @Autowired
    TempFriendRepo tempFriendRepository;

    @Autowired
    FriendRepo friendRepository;

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
    void getFriends_ReturnedListFriends_WhenFriendsExists() throws Exception{
        User user1 = new User();
        user1.setName("name1");
        user1.setEmail("email1");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("name2");
        user2.setEmail("email2");
        userRepository.save(user2);
        friendRepository.save(new Friend(0L, user1.getId(), user2.getId()));
        friendRepository.save(new Friend(0L, user2.getId(), user1.getId()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/friends")
                .header("X-User-Id", user1.getId())
                .header("X-User-Role", "USER");

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json(
                          """
                          [
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

        mockMvc.perform(request)
                .andExpectAll(
                status().isOk(),
                content().json("[]")
        );
    }

    @Test
    @Transactional
    void createRequestFriend_Returned201_WhenRequestCreate() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().string("Ваша заявка успешно отправлена")
        );
        assertTrue(tempFriendRepository.existsByUserIdAndFriendId(userId,friendId));
    }

    @Test
    @Transactional
    public void createRequestFriend_Returns409_WhenUsersAlreadyFriends() throws Exception {
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");
        friendRepository.save(new Friend(0L, userId, friendId));

        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    public void createRequestFriend_Returns409_WhenRequestAlreadySend() throws Exception {
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");
        tempFriendRepository.save(new TempFriend(0L, userId, friendId));

        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    public void createRequestFriend_Returns409_WhenRequestMeAlreadySend() throws Exception {
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/create-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");
        tempFriendRepository.save(new TempFriend(0L, friendId, userId));

        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    public void confirmRequestFriend_Returns201_WhenRequestExist() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/confirm-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");
        tempFriendRepository.save(new TempFriend(0L, userId, friendId));

        mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().string("Заявка в друзья успешно одобрена")
        );
        assertTrue(friendRepository.existsByUserIdAndFriendId(userId,friendId));
        assertTrue(friendRepository.existsByUserIdAndFriendId(friendId,userId));
    }

    @Test
    public void confirmRequestFriend_Returns404_WhenRequestNotFound() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/user/friends/confirm-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
        assertFalse(friendRepository.existsByUserIdAndFriendId(userId,friendId));
        assertFalse(friendRepository.existsByUserIdAndFriendId(friendId,userId));
    }

    @Test
    public void deleteMyRequest_Returns204_WhenRequestExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-my-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");
        tempFriendRepository.save(new TempFriend(0L, userId, friendId));

        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );
        assertFalse(tempFriendRepository.existsByUserIdAndFriendId(userId,friendId));
    }

    @Test
    public void deleteMyRequest_Returns404_WhenRequestNoExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-my-request/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
        assertFalse(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId));
    }

    @Test
    @Transactional
    public void deleteRequest_Returns204_WhenRequestExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");

        tempFriendRepository.save(new TempFriend(0L, userId, friendId));
        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );
        assertFalse(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId));
    }

    @Test
    public void deleteRequest_Returns404_WhenRequestNoExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-request/{userId}", userId)
                .header("X-User-Id", friendId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
        assertFalse(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId));
    }

    @Test
    @Transactional
    public void deleteFriend_Returns204_WhenFriendExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-friend/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");
        friendRepository.save(new Friend(0L, userId, friendId));

        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );
        assertFalse(friendRepository.existsByUserIdAndFriendId(userId, friendId));
        assertFalse(friendRepository.existsByUserIdAndFriendId(friendId, userId));
    }

    @Test
    public void deleteFriend_Returns404_WhenFriendNoExists() throws Exception{
        long userId = 1L;
        long friendId = 2L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user/friends/delete-friend/{friendId}", friendId)
                .header("X-User-Id", userId)
                .header("X-User-Role", "USER");

        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
        assertFalse(friendRepository.existsByUserIdAndFriendId(userId, friendId));
        assertFalse(friendRepository.existsByUserIdAndFriendId(friendId, userId));
    }
}
