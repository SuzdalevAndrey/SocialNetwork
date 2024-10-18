package ru.andreyszdlv.authservice.service.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingUserCleanupService {

    private final PendingUserRepo pendingUserRepository;

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void removeOldUser(){
        log.info("Executing removeOldUser");

        LocalDateTime cutOffTime = LocalDateTime
                .now()
                .minus(24, ChronoUnit.HOURS);

        pendingUserRepository.deleteBycreatedAtBefore(cutOffTime);
    }
}
