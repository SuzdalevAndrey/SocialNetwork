package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.UserFriendResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.exception.NoSuchRequestFriendException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.exception.UsersNoFriendsException;
import ru.andreyszdlv.userservice.model.Friend;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.FriendRepo;
import ru.andreyszdlv.userservice.repository.TempFriendRepo;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {

    private final UserRepo userRepository;

    private final TempFriendRepo tempFriendRepository;

    private final FriendRepo friendRepository;

    @Transactional
    public void confirmRequestFriend(Long userId, String friendEmail) {
        long friendId = getUserIdByEmail(friendEmail);

        log.info("userId: {}, friendId: {}", userId, friendId);
        if(!tempFriendRepository.existsByUserIdAndFriendId(userId, friendId)){
            throw new NoSuchRequestFriendException("errors.404.request_not_found");
        }

        Friend friend1 = new Friend();
        friend1.setUserId(userId);
        friend1.setFriendId(friendId);
        friendRepository.save(friend1);

        Friend friend2 = new Friend();
        friend2.setFriendId(userId);
        friend2.setUserId(friendId);
        friendRepository.save(friend2);

        tempFriendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    @Transactional
    public void deleteFriend(String userEmail, Long friendId) {
        long userId = getUserIdByEmail(userEmail);

        if(!friendRepository.existsByUserIdAndFriendId(userId, friendId))
            throw new UsersNoFriendsException("errors.409.users_no_friend");

        friendRepository.deleteByUserIdAndFriendId(userId, friendId);

        friendRepository.deleteByUserIdAndFriendId(friendId, userId);
    }

    public List<UserFriendResponseDTO> getFriendsByEmail(String email) {
        long userId = getUserIdByEmail(email);

        return userRepository.findUserFriends(userId);
    }

    private long getUserIdByEmail(String email){
        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                ).getId();
    }
}
