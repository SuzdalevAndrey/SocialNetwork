package ru.andreyszdlv.userservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import ru.andreyszdlv.userservice.enums.ERole;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    private final KafkaProducerService kafkaProducerService;

    private final PasswordEncoder passwordEncoder;

    private final MeterRegistry meterRegistry;

    @Transactional
    public void updateEmailUser(long userId, String newEmail) {

        log.info("Executing updateEmailUser: userId = {}, newEmail = {}",
                userId,
                newEmail);

        log.info("Getting user by id: {}", userId);
        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );

        String oldEmail = user.getEmail();

        user.setEmail(newEmail);

        log.info("Send data oldEmail: {}, newEmail: {} in kafka for update email event",
                oldEmail,
                newEmail
        );
        kafkaProducerService.sendEditEmailEvent(oldEmail, newEmail);
    }

    @Transactional
    public void updatePasswordUser(long userId, String oldPassword, String newPassword) {

        log.info("Executing updatePasswordUser for userId: {}", userId);

        log.info("Getting user by userId: {}", userId);
        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );

        log.info("Checking password comparison for userId: {}", userId);
        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            log.info("Updating password for userId: {}", userId);
            user.setPassword(passwordEncoder.encode(newPassword));

            log.info("Send data email: {} in kafka for update password event", user.getEmail());
            kafkaProducerService.sendEditPasswordEvent(user.getEmail());

            meterRegistry.counter("user_change_password").increment();
        }
        else{

            log.error("User password and the received password do not match");

            throw new DifferentPasswordsException("errors.409.invalid_password");
        }
    }
}
