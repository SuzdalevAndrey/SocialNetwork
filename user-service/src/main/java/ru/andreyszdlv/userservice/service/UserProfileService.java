package ru.andreyszdlv.userservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.mapper.UserMapper;
import ru.andreyszdlv.userservice.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final PasswordEncoder passwordEncoder;

    private final MeterRegistry meterRegistry;

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfileById(long userId) {
        log.info("Executing getUserById for id: {}", userId);

        User user = userService.getUserByIdOrThrow(userId);

        return userMapper.userToUserResponseDTO(user);
    }

    @Transactional
    public void updateEmailUser(long userId, String newEmail) {

        log.info("Executing updateEmailUser: userId = {}, newEmail = {}",
                userId,
                newEmail);

        User user = userService.getUserByIdOrThrow(userId);

        String oldEmail = user.getEmail();

        user.setEmail(newEmail);

        log.info("Send data oldEmail: {}, newEmail: {} in kafka for update email event",
                oldEmail,
                newEmail
        );
        applicationEventPublisher.publishEvent(new EditEmailKafkaDTO(oldEmail, newEmail));
    }

    @Transactional
    public void updatePasswordUser(long userId, String oldPassword, String newPassword) {

        log.info("Executing updatePasswordUser for userId: {}", userId);

        User user = userService.getUserByIdOrThrow(userId);

        log.info("Checking password comparison for userId: {}", userId);
        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            log.info("Updating password for userId: {}", userId);
            user.setPassword(passwordEncoder.encode(newPassword));

            log.info("Send data email: {} in kafka for update password event", user.getEmail());
            applicationEventPublisher.publishEvent(new EditPasswordKafkaDTO(user.getEmail()));

            meterRegistry.counter("user_change_password").increment();
        }
        else{

            log.error("User password and the received password do not match");

            throw new DifferentPasswordsException("errors.409.invalid_password");
        }
    }
}
