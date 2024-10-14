package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;

@Service
@RequiredArgsConstructor
public class RegisterCompensationService {

    private final PendingUserRepo pendingUserRepository;

    @Transactional
    public void handle(String email){
        pendingUserRepository.deleteByEmail(email);
    }
}
