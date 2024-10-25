package ru.andreyszdlv.userservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepo userRepository;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Mock
    MeterRegistry meterRegistry;

    @Mock
    Counter counter;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateEmailUser_Success_WhenUserExists() {
        long userId = 1L;
        String oldEmail = "oldEmail@email.com";
        String newEmail = "newEmail@email.com";
        User user = new User();
        user.setEmail(oldEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        userService.updateEmailUser(userId, newEmail);

        verify(userRepository, times(1)).findById(userId);
        verify(kafkaProducerService, times(1)).sendEditEmailEvent(oldEmail, newEmail);
    }

    @Test
    void updateEmailUser_Failed_WhenUserNoExists() {
        long userId = 1L;
        String oldEmail = "oldEmail@email.com";
        String newEmail = "newEmail@email.com";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(
                NoSuchUserException.class,
                ()->userService.updateEmailUser(userId, newEmail)
        );

        verify(userRepository, times(1)).findById(userId);
        verify(kafkaProducerService, never()).sendEditEmailEvent(oldEmail, newEmail);
    }

    @Test
    void updatePasswordUser_Success_WhenUserExists() {
        long userId = 1L;
        String oldPassword = "000000";
        String newPassword = "0000000";
        String email = "email@email.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword(oldPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(user.getPassword(), oldPassword)).thenReturn(true);
        when(meterRegistry.counter("user_change_password")).thenReturn(counter);
        userService.updatePasswordUser(userId, oldPassword, newPassword);

        verify(userRepository, times(1)).findById(userId);
        verify(kafkaProducerService, times(1)).sendEditPasswordEvent(email);
    }

    @Test
    void updatePasswordUser_Failed_WhenUserNoExists() {
        long userId = 1L;
        String oldPassword = "000000";
        String newPassword = "0000000";
        String email = "email@email.com";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(
                NoSuchUserException.class,
                ()->userService.updatePasswordUser(userId, oldPassword, newPassword)
        );

        verify(userRepository, times(1)).findById(userId);
        verify(kafkaProducerService, never()).sendEditPasswordEvent(email);
    }

    @Test
    void updatePasswordUser_Failed_WhenPasswordsDifferent() {
        long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String email = "email@email.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(oldPassword));

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(user.getPassword(), "differentOldPassword")).thenReturn(true);
        assertThrows(
                DifferentPasswordsException.class,
                ()->userService.updatePasswordUser(userId, "differentOldPassword", newPassword)
        );

        verify(userRepository, times(1)).findById(userId);
        verify(kafkaProducerService, never()).sendEditPasswordEvent(email);
    }

}