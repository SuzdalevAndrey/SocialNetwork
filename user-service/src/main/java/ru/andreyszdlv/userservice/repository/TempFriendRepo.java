package ru.andreyszdlv.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.userservice.model.TempFriend;

public interface TempFriendRepo extends JpaRepository<TempFriend, Long> {

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
