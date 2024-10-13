package ru.andreyszdlv.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.authservice.model.PendingUser;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PendingUserRepo extends JpaRepository<PendingUser, Long> {
    Optional<PendingUser> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteBycreatedAtBefore(LocalDateTime cutOffTime);

    void deleteByEmail(String email);
}
