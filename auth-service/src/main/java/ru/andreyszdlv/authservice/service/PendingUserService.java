package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.authservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.authservice.dto.kafkadto.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.enums.ERole;
import ru.andreyszdlv.authservice.model.PendingUser;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingUserService {

    private final UserServiceFeignClient userServiceFeignClient;

    private final PendingUserRepo pendingUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final KafkaProducerService kafkaProducerService;

    @Transactional(readOnly = true)
    public boolean existsUserByEmail(String email) {
        return pendingUserRepository.existsByEmail(email);
    }

    @Transactional
    public void savePendingUser(String name, String email, String password){

        PendingUser user = new PendingUser();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(ERole.USER);
        user.setCreatedAt(LocalDateTime.now());

        pendingUserRepository.save(user);
    }

    @Transactional
    public void savePendingUserInPermanentBD(String email){

        log.info("Getting a user from a table pendings user by email: {}", email);

        PendingUser pendingUser = pendingUserRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchElementException("errors.404.email_not_found")
                );

        pendingUserRepository.deleteByEmail(email);

        kafkaProducerService.sendSaveUserEvent(
                UserDetailsKafkaDTO
                        .builder()
                        .name(pendingUser.getName())
                        .email(pendingUser.getEmail())
                        .password(pendingUser.getPassword())
                        .role(pendingUser.getRole())
                        .build()
        );

    }
}
