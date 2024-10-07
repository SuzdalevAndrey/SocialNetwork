package ru.andreyszdlv.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.authservice.model.EmailVerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepo
        extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByEmail(String email);

    void deleteByexpirationTimeBefore(LocalDateTime cutOffTime);
}
