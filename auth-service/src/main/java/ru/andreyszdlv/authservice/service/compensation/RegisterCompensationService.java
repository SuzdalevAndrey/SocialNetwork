package ru.andreyszdlv.authservice.service.compensation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterCompensationService {

    private final PendingUserRepo pendingUserRepository;

    @Transactional
    public void handle(String email){
        log.info("Executing handle in RegisterCompensationService");

        log.info("Delete user by email: {} from Pending user table", email);
        pendingUserRepository.deleteByEmail(email);
    }
}
