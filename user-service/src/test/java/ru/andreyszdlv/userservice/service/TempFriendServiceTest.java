package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.userservice.exception.NoSuchRequestFriendException;
import ru.andreyszdlv.userservice.exception.RequestInFriendsAlreadySendException;
import ru.andreyszdlv.userservice.exception.UsersAlreadyFriendsException;
import ru.andreyszdlv.userservice.model.TempFriend;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TempFriendServiceTest {

    @Mock
    FriendRepo friendRepository;

    @Mock
    TempFriendRepo tempFriendRepository;

    @InjectMocks
    TempFriendService tempFriendService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRequestFriend_Failed_WhenUserAlreadyFriends(){
        long userId = 1L;
        long friendId = 2L;
        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        TempFriend tempFriend = new TempFriend();
        tempFriend.setUserId(userId);
        tempFriend.setFriendId(friendId);

        assertThrows(
                UsersAlreadyFriendsException.class,
                ()->tempFriendService.createRequestFriend(userId, friendId)
        );
        verify(tempFriendRepository, never()).save(eq(tempFriend));
    }

    @Test
    void createRequestFriend_Failed_WhenRequestAlreadyExists(){
        long userId = 1L;
        long friendId = 2L;
        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);
        TempFriend tempFriend = new TempFriend();
        tempFriend.setUserId(userId);
        tempFriend.setFriendId(friendId);

        assertThrows(
                RequestInFriendsAlreadySendException.class,
                ()->tempFriendService.createRequestFriend(userId, friendId)
        );
        verify(tempFriendRepository, never()).save(eq(tempFriend));
    }

    @Test
    void createRequestFriend_Failed_WhenRequestMeAlreadyExists(){
        long userId = 1L;
        long friendId = 2L;
        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId)).thenReturn(true);
        TempFriend tempFriend = new TempFriend();
        tempFriend.setUserId(userId);
        tempFriend.setFriendId(friendId);

        assertThrows(
                RequestInFriendsAlreadySendException.class,
                ()->tempFriendService.createRequestFriend(userId, friendId)
        );
        verify(tempFriendRepository, never()).save(eq(tempFriend));
    }
    @Test
    void createRequestFriend_Success_WhenDataIsValid(){
        long userId = 1L;
        long friendId = 2L;
        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);
        when(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId)).thenReturn(false);
        TempFriend tempFriend = new TempFriend();
        tempFriend.setUserId(userId);
        tempFriend.setFriendId(friendId);

        tempFriendService.createRequestFriend(userId, friendId);

        verify(tempFriendRepository, times(1)).save(eq(tempFriend));
    }

    @Test
    void deleteMyRequest_Failed_WhenRequestNoExists(){
        long userId = 1L;
        long friendId = 2L;
        when(tempFriendRepository.existsByUserIdAndFriendId(userId,friendId)).thenReturn(false);

        assertThrows(
                NoSuchRequestFriendException.class,
                ()->tempFriendService.deleteMyRequest(userId, friendId)
        );
        verify(tempFriendRepository, never()).deleteByUserIdAndFriendId(userId, friendId);
    }

    @Test
    void deleteMyRequest_Success_WhenDataIsValid(){
        long userId = 1L;
        long friendId = 2L;
        when(tempFriendRepository.existsByUserIdAndFriendId(userId,friendId)).thenReturn(true);

        tempFriendService.deleteMyRequest(userId, friendId);

        verify(tempFriendRepository, times(1)).deleteByUserIdAndFriendId(userId, friendId);
    }

    @Test
    void deleteRequest_Failed_WhenRequestNoExists(){
        long userId = 1L;
        long friendId = 2L;
        when(tempFriendRepository.existsByUserIdAndFriendId(userId,friendId)).thenReturn(false);

        assertThrows(
                NoSuchRequestFriendException.class,
                ()->tempFriendService.deleteRequest(userId, friendId)
        );
        verify(tempFriendRepository, never()).deleteByUserIdAndFriendId(userId, friendId);
    }

    @Test
    void deleteRequest_Success_WhenDataIsValid(){
        long userId = 1L;
        long friendId = 2L;
        when(tempFriendRepository.existsByUserIdAndFriendId(userId,friendId)).thenReturn(true);

        tempFriendService.deleteRequest(userId, friendId);

        verify(tempFriendRepository, times(1)).deleteByUserIdAndFriendId(userId, friendId);
    }
}