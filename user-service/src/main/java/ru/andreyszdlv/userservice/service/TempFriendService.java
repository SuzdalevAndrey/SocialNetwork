package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.exception.NoSuchRequestFriendException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.exception.RequestInFriendsAlreadySendException;
import ru.andreyszdlv.userservice.exception.UsersAlreadyFriendsException;
import ru.andreyszdlv.userservice.model.TempFriend;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;
import ru.andreyszdlv.userservice.repository.UserRepo;

@Service
@RequiredArgsConstructor
public class TempFriendService {

    private final UserRepo userRepository;

    private final FriendRepo friendRepository;

    private final TempFriendRepo tempFriendRepository;

    @Transactional
    public void createRequestFriend(String userEmail, Long friendId) {
        Long userId = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                ).getId();

        if(friendRepository.existsByUserIdAndFriendId(userId, friendId)){
            throw new UsersAlreadyFriendsException("errors.409.users_already_friends");
        }

        if(tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            throw new RequestInFriendsAlreadySendException("errors.409.request_already_send");
        }

        if(tempFriendRepository.existsByUserIdAndFriendId(friendId, userId)){
            throw new RequestInFriendsAlreadySendException("errors.409.request_me_already_send");
        }

        TempFriend tempFriend = new TempFriend();
        tempFriend.setUserId(userId);
        tempFriend.setFriendId(friendId);

        tempFriendRepository.save(tempFriend);
    }

    @Transactional
    public void deleteMyRequest(String userEmail, Long friendId) {
        Long userId = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                ).getId();

        if(!tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            throw new NoSuchRequestFriendException("errors.404.request_not_found");
        }

        tempFriendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    @Transactional
    public void deleteRequest(Long userId, String friendEmail) {
        Long friendId = userRepository
                .findByEmail(friendEmail)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                ).getId();

        if(!tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            throw new NoSuchRequestFriendException("errors.404.request_not_found");
        }

        tempFriendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }
}
