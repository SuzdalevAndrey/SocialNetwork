package ru.andreyszdlv.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andreyszdlv.userservice.dto.controller.FriendResponseDTO;
import ru.andreyszdlv.userservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT new ru.andreyszdlv.userservice.dto.controller.FriendResponseDTO(u.name, u.idImage) " +
            "FROM User u JOIN Friend f ON u.id = f.friendId WHERE f.userId = :userId")
    List<FriendResponseDTO> findUserFriends(@Param("userId") Long userId);
}
