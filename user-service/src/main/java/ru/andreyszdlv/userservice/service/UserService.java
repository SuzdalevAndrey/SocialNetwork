package ru.andreyszdlv.userservice.service;

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

    @Transactional
    public void updateEmailUser(String oldEmail, String newEmail) {

        log.info("Executing updateEmailUser: oldEmail = {}, newEmail = {}",
                oldEmail,
                newEmail);

        log.info("Getting user by email: {}", oldEmail);
        User user = userRepository
                .findByEmail(oldEmail)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );

        user.setEmail(newEmail);

        log.info("Send data oldEmail: {}, newEmail: {} in kafka for update email event",
                oldEmail,
                newEmail
        );
        kafkaProducerService.sendEditEmailEvent(oldEmail, newEmail);
    }

    @Transactional
    public void updatePasswordUser(String userEmail, String oldPassword, String newPassword) {

        log.info("Executing updatePasswordUser for email: {}", userEmail);

        log.info("Getting user by email: {}", userEmail);
        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );

        log.info("Checking password comparison for user email: {}", userEmail);
        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            log.info("Updating password for user email: {}", userEmail);
            user.setPassword(passwordEncoder.encode(newPassword));

            log.info("Send data email: {} in kafka for update password event", userEmail);
            kafkaProducerService.sendEditPasswordEvent(userEmail);
        }
        else{

            log.error("User password and the received password do not match");

            throw new DifferentPasswordsException("errors.400.invalid_password");
        }
    }

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        log.info("Executing getUserIdByEmail for email: {}", email);

        log.info("Getting a userId by email: {}", email);
        Long userId = userRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                )
                .getId();

        return userId;
    }

    @Transactional(readOnly = true)
    public String getUserEmailByUserId(long userId) {
        log.info("Executing getUserEmailByUserId for userId: {}", userId);

        log.info("Getting email by userId: {}", userId);
        String email = userRepository
                .findById(userId)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                )
                .getEmail();

        return email;
    }

    @Transactional(readOnly = true)
    public String getNameByUserEmail(String email) {
        log.info("Executing getNameByUserEmail for email: {}", email);

        log.info("Getting name by email: {}", email);
        String name = userRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                )
                .getName();

        return name;
    }

    @Transactional(readOnly = true)
    public UserDetailsResponseDTO getUserDetailsByEmail(String email) {
        log.info("Executing getUserDetailsByEmail for email: {}", email);

        log.info("Getting user by email: {}", email);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                    ()->new NoSuchUserException("errors.404.user_not_found")
                );

        return UserDetailsResponseDTO
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
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
            userRepository.save(user);
        }
        catch (Exception ex){
            log.error("Send data name, email: {}, password, role in kafka for failure save user event",
                    email
            );
            kafkaProducerService.sendFailureSaveUserEvent(
                    name,
                    email,
                    password,
                    role
            );
        }
    }

    @Transactional(readOnly = true)
    public Boolean existsUserByEmail(String email) {
        log.info("Executing existsUserByEmail for email: {}", email);
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUserEmail(String email) {
        log.info("Executing getUserByUserEmail for email: {}", email);

        log.info("Getting user by email: {}", email);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                    ()->new NoSuchUserException("errors.404.user_not_found")
                );

        return UserResponseDTO
                .builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
