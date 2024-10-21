package ru.andreyszdlv.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.userservice.model.Friend;

import java.util.List;

public interface FriendRepo extends JpaRepository<Friend, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    void deleteByUserIdAndFriendId(Long userId, Long friendId);

    List<Friend> findAllByUserId(Long userId);
}
