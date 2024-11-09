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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    UserRepo userRepository;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserByIdOrThrow_ReturnUser_WhenUserExists() {
        long userId = 1L;
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        User response = userService.getUserByIdOrThrow(userId);

        assertNotNull(response);
        assertEquals(user, response);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByIdOrThrow_ThrowException_WhenUserNotExists() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenThrow(NoSuchUserException.class);

        assertThrows(
                NoSuchUserException.class,
                () -> userService.getUserByIdOrThrow(userId)
        );

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByEmaildOrThrow_ReturnUser_WhenUserExists(){
        String email = "test@mail.ru";
        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        User response = userService.getUserByEmaildOrThrow(email);

        assertNotNull(response);
        assertEquals(user, response);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getUserByEmaildOrThrow_ThrowException_WhenUserNotExists(){
        String email = "test@mail.ru";
        when(userRepository.findByEmail(email)).thenThrow(NoSuchUserException.class);

        assertThrows(
                NoSuchUserException.class,
                ()->userService.getUserByEmaildOrThrow(email)
        );

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void save_Success(){
        User user = mock(User.class);

        userService.save(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void existsByEmail_ReturnsTrue_WhenUserExists(){
        String email = "test@mail.ru";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean response = userService.existsByEmail(email);

        assertTrue(response);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_ReturnsFalse_WhenUserNotExists(){
        String email = "test@mail.ru";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean response = userService.existsByEmail(email);

        assertFalse(response);
        verify(userRepository, times(1)).existsByEmail(email);
    }
}