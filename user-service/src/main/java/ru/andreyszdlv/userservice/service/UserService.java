package ru.andreyszdlv.userservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controllerDto.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controllerDto.UserResponseDTO;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import ru.andreyszdlv.userservice.security.enums.ERole;

import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    private final KafkaProducerService kafkaProducerService;


    @Transactional
    public void updateEmailUser(String oldEmail, String newEmail)
            throws NoSuchElementException{

        log.info("Executing updateEmailUser in UserService");

        log.info("Verification of the user existence");
        User user = userRepository.findByEmail(oldEmail)
                .orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"));

        user.setEmail(newEmail);

        kafkaProducerService.sendEditEmailEvent(oldEmail, newEmail);

        log.info("The old email: {} has been updated to a new: {}", user.getEmail(), newEmail);
    }

    @Transactional
    public void updatePasswordUser(String userEmail, String oldPassword, String newPassword)
            throws BadCredentialsException {

        log.info("Executing updatePasswordUser in UserService");

        log.info("Verification of the user existence");
        User user = userRepository.findByEmail(userEmail).
                orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));

            kafkaProducerService.sendEditPasswordEvent(userEmail);

            log.info("Successful update password");
        }
        else{

            log.error("The user's password and the received password do not match");

            throw new BadCredentialsException("errors.400.invalid_password");
        }
    }

    public Long getUserIdByEmail(String email) {
        log.info("Executing getUserIdByEmail in UserService");

        log.info("Getting a userId by email");
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"))
                .getId();

        log.info("Successful get a userId: {} by email", userId);

        return userId;
    }

    public String getUserEmailByUserId(long userId) {

        log.info("Executing getUserEmailByUserId in UserService");

        log.info("Getting a email by userId: {}", userId);
        String email = userRepository
                .findById(userId)
                .orElseThrow(
                        ()->new NoSuchElementException("errors.404.user_not_found")
                )
                .getEmail();

        log.info("Successful get a email: {} by userId: {}", email, userId);

        return email;
    }

    public String getNameByUserEmail(String email) {
        log.info("Executing getNameByUserEmail in UserService");

        log.info("Getting a name by email");
        String name = userRepository.findByEmail(email)
                .orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"))
                .getName();

        log.info("Successful get a name: {} by email", name);

        return name;
    }

    public UserDetailsResponseDTO getUserDetailsByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                ()->new NoSuchElementException("errors.404.user_not_found")
        );

        return UserDetailsResponseDTO
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    public void saveUser(String name, String email, String password, ERole role) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);
    }

    public Boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserResponseDTO getUserByUserEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                ()->new NoSuchElementException("errors.404.user_not_found")
        );
        return UserResponseDTO
                .builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
