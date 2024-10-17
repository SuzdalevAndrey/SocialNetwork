package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.enums.ERole;
import ru.andreyszdlv.authservice.model.PendingUser;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingUserService {

    private final PendingUserRepo pendingUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final KafkaProducerService kafkaProducerService;

    @Transactional(readOnly = true)
    public boolean existsUserByEmail(String email) {
        log.info("Executing existsUserByEmail in PendingUserService");
        return pendingUserRepository.existsByEmail(email);
    }

    @Transactional
    public void savePendingUser(String name, String email, String password){
        log.info("Executing savePendingUser in PendingUserService with email: {}", email);

        PendingUser user = new PendingUser();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(ERole.USER);
        user.setCreatedAt(LocalDateTime.now());

        log.info("Saving pending user with email: {}", email);
        pendingUserRepository.save(user);
    }

    @Transactional
    public void savePendingUserInPermanentBD(String email){
        log.info("Executing savePendingUserInPermanentBD in PendingUserService with email: {}",
                email);

        log.info("Getting user from pending user table by email: {}", email);
        PendingUser pendingUser = pendingUserRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchElementException("errors.404.email_not_found")
                );

        log.info("Delete user from pending user table by email: {}", email);
        pendingUserRepository.deleteByEmail(email);

        log.info("Send user in kafka for save in permanent BD");
        kafkaProducerService.sendSaveUserEvent(
                pendingUser.getName(),
                pendingUser.getEmail(),
                pendingUser.getPassword(),
                pendingUser.getRole()
        );

    }
}
