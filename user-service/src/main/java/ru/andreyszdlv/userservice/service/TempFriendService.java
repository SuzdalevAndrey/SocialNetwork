package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.exception.NoSuchRequestFriendException;
import ru.andreyszdlv.userservice.exception.RequestInFriendsAlreadySendException;
import ru.andreyszdlv.userservice.exception.UsersAlreadyFriendsException;
import ru.andreyszdlv.userservice.model.TempFriend;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class TempFriendService {

    private final FriendRepo friendRepository;

    private final TempFriendRepo tempFriendRepository;

    @Transactional
    public void createRequestFriend(long userId, long friendId) {
        log.info("Executing createRequestFriend for userId={}, friendId={}", userId, friendId);

        log.info("Friendship checking: userId={}, friendId={}", userId, friendId);
        if(friendRepository.existsByUserIdAndFriendId(userId, friendId)){
            log.error("Users with userId={} and friendId={} are already friends", userId, friendId);
            throw new UsersAlreadyFriendsException("errors.409.users_already_friends");
        }

        log.info("Request already send checking: userId={}, friendId={}", userId, friendId);
        if(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            log.error("Request already send: userId={}, friendId={}", userId, friendId);
            throw new RequestInFriendsAlreadySendException("errors.409.request_already_send");
        }

        log.info("Request me already send checking: userId={}, friendId={}", userId, friendId);
        if(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId)){
            log.error("Request me already send: userId={}, friendId={}", userId, friendId);
            throw new RequestInFriendsAlreadySendException("errors.409.request_me_already_send");
        }

        TempFriend tempFriend = new TempFriend();
        tempFriend.setUserId(userId);
        tempFriend.setFriendId(friendId);

        log.info("Saving request: userId={}, friendId={}", userId, friendId);
        tempFriendRepository.save(tempFriend);
    }

    @Transactional
    public void deleteMyRequest(long userId, long friendId) {
        log.info("Executing deleteMyRequest for userId={}, friendId={}", userId, friendId);

        log.info("Checking exist request: userId={}, friendId={}", userId, friendId);
        if(!tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            log.error("This request not found: userId={}, friendId={}", userId, friendId);
            throw new NoSuchRequestFriendException("errors.404.request_not_found");
        }

        log.info("Deleting request: userId={}, friendId={}", userId, friendId);
        tempFriendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    @Transactional
    public void deleteRequest(long userId, long friendId) {
        log.info("Executing deleteRequest for userId={}, friendId={}", userId, friendId);

        log.info("Checking exist request: userId={}, friendId={}", userId, friendId);
        if(!tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            log.error("This request not found: userId={}, friendId={}", userId, friendId);
            throw new NoSuchRequestFriendException("errors.404.request_not_found");
        }

        log.info("Deleting request: userId={}, friendId={}", userId, friendId);
        tempFriendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }
}
