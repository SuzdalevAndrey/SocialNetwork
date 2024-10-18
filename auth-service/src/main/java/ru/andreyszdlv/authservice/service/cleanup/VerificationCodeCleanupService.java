package ru.andreyszdlv.authservice.service.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.repository.EmailVerificationCodeRepo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeCleanupService {

    private final EmailVerificationCodeRepo emailVerificationCodeRepository;

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void removeExpiredVerificationCodes(){
        log.info("Executing removeExpiredVerificationCodes");

        LocalDateTime cutOffTime = LocalDateTime
                .now();

        emailVerificationCodeRepository.deleteByexpirationTimeBefore(cutOffTime);
    }
}
