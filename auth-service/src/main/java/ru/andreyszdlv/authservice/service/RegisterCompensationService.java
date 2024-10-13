package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;

@Service
@RequiredArgsConstructor
public class RegisterCompensationService {

    private final PendingUserRepo pendingUserRepository;

    public void handle(String email){
        pendingUserRepository.deleteByEmail(email);
    }
}
