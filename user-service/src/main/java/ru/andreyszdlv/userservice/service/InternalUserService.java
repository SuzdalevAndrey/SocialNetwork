package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.mapper.UserMapper;
import ru.andreyszdlv.userservice.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternalUserService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final UserMapper userMapper;

    private final UserService userService;

    @Transactional(readOnly = true)
    public String getUserEmailByUserId(long userId) {
        log.info("Executing getUserEmailByUserId for userId: {}", userId);

        log.info("Getting email by userId: {}", userId);
        String email = userService
                .getUserByIdOrThrow(userId)
                .getEmail();

        return email;
    }

    @Transactional(readOnly = true)
    public UserDetailsResponseDTO getUserDetailsByEmail(String email) {
        log.info("Executing getUserDetailsByEmail for email: {}", email);

        log.info("Getting user by email: {}", email);
        User user = userService.getUserByEmaildOrThrow(email);

        return userMapper.userToUserDetailsResponseDTO(user);
    }

    @Transactional
    public void saveUser(String name, String email, String password, ERole role) {
        log.info("Executing saveUser for email: {}", email);

        User user = new User();

        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRole(role);

        log.info("Saving user with email: {}", email);
        try {
            userService.save(user);
        } catch (Exception ex) {
            log.error("Send data name, email: {}, password, role in kafka for failure save user event",
                    email
            );
            applicationEventPublisher.publishEvent(
                    UserDetailsKafkaDTO
                            .builder()
                            .name(name)
                            .email(email)
                            .password(password)
                            .role(role)
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
    public boolean existsUserByEmail(String email) {
        log.info("Executing existsUserByEmail for email: {}", email);
        return userService.existsByEmail(email);
    }
}