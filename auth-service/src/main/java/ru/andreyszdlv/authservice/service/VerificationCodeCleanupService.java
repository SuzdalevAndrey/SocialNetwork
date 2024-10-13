package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.repository.EmailVerificationCodeRepo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VerificationCodeCleanupService {

    private final EmailVerificationCodeRepo emailVerificationCodeRepository;

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void removeExpiredVerificationCodes(){
        LocalDateTime cutOffTime = LocalDateTime
                .now();

        emailVerificationCodeRepository.deleteByexpirationTimeBefore(cutOffTime);
    }
}
