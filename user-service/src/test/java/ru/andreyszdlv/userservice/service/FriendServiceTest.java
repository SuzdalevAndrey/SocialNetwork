package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.userservice.dto.controller.FriendResponseDTO;
import ru.andreyszdlv.userservice.exception.NoSuchRequestFriendException;
import ru.andreyszdlv.userservice.exception.UsersNoFriendsException;
import ru.andreyszdlv.userservice.model.Friend;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FriendServiceTest {

    @Mock
    TempFriendRepo tempFriendRepository;

    @Mock
    UserRepo userRepository;

    @Mock
    FriendRepo friendRepository;

    @InjectMocks
    FriendService friendService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void confirmRequestFriend_RequestNotExists(){
        long userId = 1L;
        long friendId = 2L;

        when(tempFriendRepository.existsByUserIdAndFriendId(userId,friendId)).thenReturn(false);

        assertThrows(
                NoSuchRequestFriendException.class,
                () -> friendService.confirmRequestFriend(userId, friendId)
        );
        verify(friendRepository, never()).save(any(Friend.class));
        verify(tempFriendRepository, never()).deleteByUserIdAndFriendId(userId, friendId);
    }

    @Test
    public void confirmRequestFriend_RequestExists(){
        long userId = 1L;
        long friendId = 2L;

        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        friendService.confirmRequestFriend(userId, friendId);

        verify(friendRepository, times(2)).save(any(Friend.class));
        verify(tempFriendRepository, times(1)).deleteByUserIdAndFriendId(userId, friendId);
    }

    @Test
    public void deleteFriend_FriendExists(){
        long userId = 1L;
        long friendId = 2L;

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);

        assertThrows(
                UsersNoFriendsException.class,
                () -> friendService.deleteFriend(userId, friendId)
        );
        verify(friendRepository, never()).deleteByUserIdAndFriendId(userId, friendId);
        verify(friendRepository, never()).deleteByUserIdAndFriendId(friendId, userId);
    }

    @Test
    public void deleteFriend_FriendNotExists(){
        long userId = 1L;
        long friendId = 2L;

        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        friendService.deleteFriend(userId, friendId);

        verify(friendRepository, times(1)).deleteByUserIdAndFriendId(userId, friendId);
        verify(friendRepository, times(1)).deleteByUserIdAndFriendId(friendId, userId);
    }

    @Test
    public void getFriendsByUserId_ReturnedListFriends(){
        long userId = 1L;
        FriendResponseDTO mockFriend = new FriendResponseDTO("Name","Email");
        List<FriendResponseDTO> mockFriends = List.of(mockFriend);

        when(userRepository.findUserFriends(userId)).thenReturn(mockFriends);
        List<FriendResponseDTO> friends = friendService.getFriendsByUserId(userId);

        verify(userRepository, times(1)).findUserFriends(userId);
        assertNotNull(friends);
        assertEquals(friends, mockFriends);
        assertEquals(friends.size(), mockFriends.size());
    }

    @Test
    public void getFriendsByUserId_NoFriends(){
        long userId = 1L;

        when(userRepository.findUserFriends(userId)).thenReturn(List.of());
        List<FriendResponseDTO> friends = friendService.getFriendsByUserId(userId);

        verify(userRepository, times(1)).findUserFriends(userId);
        assertNotNull(friends);
        assertEquals(friends.size(), 0);
        assertTrue(friends.isEmpty());
    }
}
