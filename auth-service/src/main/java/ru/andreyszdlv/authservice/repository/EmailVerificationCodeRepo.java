package ru.andreyszdlv.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.authservice.model.EmailVerificationCode;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationCodeRepo
        extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findByEmail(String email);

    void deleteByexpirationTimeBefore(LocalDateTime cutOffTime);
}
