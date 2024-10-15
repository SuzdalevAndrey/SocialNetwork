package ru.andreyszdlv.authservice.service.compensation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.enums.ERole;
import ru.andreyszdlv.authservice.model.PendingUser;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmEmailCompensationService {

    private final PendingUserRepo pendingUserRepository;

    @Transactional
    public void handle(String name, String email, String password, ERole role){
        PendingUser user = new PendingUser();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        pendingUserRepository.save(user);
    }
}
