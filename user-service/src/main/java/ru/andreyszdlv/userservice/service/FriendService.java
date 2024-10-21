package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.UserFriendResponseDTO;
import ru.andreyszdlv.userservice.exception.NoSuchRequestFriendException;
import ru.andreyszdlv.userservice.exception.UsersNoFriendsException;
import ru.andreyszdlv.userservice.model.Friend;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {

    private final UserRepo userRepository;

    private final TempFriendRepo tempFriendRepository;

    private final FriendRepo friendRepository;

    @Transactional
    public void confirmRequestFriend(long userId, long friendId) {
        log.info("Executing confirmRequestFriend for userId={}, friendId={}", userId, friendId);

        log.info("Request send checking: userId={}, friendId={}", userId, friendId);
        if(!tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            log.error("Request no send: userId={}, friendId={}", userId, friendId);
            throw new NoSuchRequestFriendException("errors.404.request_not_found");
        }

        log.info("Saving friends for userId={}, friendId={}", userId, friendId);
        Friend friend1 = new Friend();
        friend1.setUserId(userId);
        friend1.setFriendId(friendId);
        friendRepository.save(friend1);

        Friend friend2 = new Friend();
        friend2.setFriendId(userId);
        friend2.setUserId(friendId);
        friendRepository.save(friend2);

        log.info("Deleting request friendship for userId={}, friendId={}", userId, friendId);
        tempFriendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    @Transactional
    public void deleteFriend(long userId, long friendId) {
        log.info("Executing deleteFriend for userId={}, friendId={}", userId, friendId);

        log.info("Friendship checking: userId={}, friendId={}", userId, friendId);
        if(!friendRepository.existsByUserIdAndFriendId(userId, friendId)){
            log.error("Users no friends: userId={}, friendId={}", userId, friendId);
            throw new UsersNoFriendsException("errors.409.users_no_friend");
        }

        log.info("Deleting friend: userId={}, friendId={}", userId, friendId);
        friendRepository.deleteByUserIdAndFriendId(userId, friendId);

        log.info("Deleting friend: userId={}, friendId={}", friendId, userId);
        friendRepository.deleteByUserIdAndFriendId(friendId, userId);
    }

    public List<UserFriendResponseDTO> getFriendsByUserId(long userId) {
        log.info("Executing getFriendsById for userId={}", userId);
        return userRepository.findUserFriends(userId);
    }

}
