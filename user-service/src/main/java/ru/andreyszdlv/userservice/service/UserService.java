package ru.andreyszdlv.userservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    private final KafkaProducerService kafkaProducerService;

    private String getEmailAuthenticationUser() throws UsernameNotFoundException {

        log.info("Executing getEmailAuthenticationUser in UserService");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetails) {

                String email = ((UserDetails) authentication.getPrincipal()).getUsername();
                log.info("User is authenticated, email: {}", email);

                return email;
            }
        }

        log.error("User not authenticated or authentication is null");
        throw new NoSuchElementException("errors.404.user_not_found");
    }


    @Transactional
    public void updateEmailUser(String newEmail)
            throws NoSuchElementException{

        log.info("Executing updateEmailUser in UserService");

        String oldEmail = getEmailAuthenticationUser();

        log.info("Verification of the user existence");
        User user = userRepository.findByEmail(oldEmail)
                .orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"));

        user.setEmail(newEmail);

        kafkaProducerService.sendEditEmailEvent(oldEmail, newEmail);

        log.info("The old email: {} has been updated to a new: {}", user.getEmail(), newEmail);
    }

    @Transactional
    public void updatePasswordUser(String oldPassword, String newPassword)
            throws BadCredentialsException {

        log.info("Executing updatePasswordUser in UserService");

        String email = getEmailAuthenticationUser();

        log.info("Verification of the user existence");
        User user = userRepository.findByEmail(email).
                orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));

            kafkaProducerService.sendEditPasswordEvent(email);

            log.info("Successful update password");
        }
        else{

            log.error("The user's password and the received password do not match");

            throw new BadCredentialsException("errors.400.invalid_password");
        }
    }

    public Long getUserIdByEmail() {
        log.info("Executing getUserIdByEmail in UserService");

        log.info("Getting a userId by email");
        Long userId = userRepository.findByEmail(getEmailAuthenticationUser())
                .orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"))
                .getId();

        log.info("Successful get a userId: {} by email", userId);

        return userId;
    }
}
