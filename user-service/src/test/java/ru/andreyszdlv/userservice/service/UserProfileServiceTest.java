package ru.andreyszdlv.userservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.mapper.UserMapper;
import ru.andreyszdlv.userservice.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Mock
    MeterRegistry meterRegistry;

    @Mock
    Counter counter;

    @Mock
    UserService userService;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserProfileById_ReturnUserResponseDTO_WhenUserExists(){
        long userId = 1L;
        User user = mock(User.class);
        UserResponseDTO userResponseDTO = new UserResponseDTO(
                "name",
                "email",
                null
        );
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(userMapper.userToUserResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO response = userProfileService.getUserProfileById(userId);

        assertNotNull(response);
        assertEquals(userResponseDTO, response);
        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(userMapper, times(1)).userToUserResponseDTO(user);
    }

    @Test
    void getUserProfileById_ThrowException_WhenUserNotExists(){
        long userId = 1L;
        User user = mock(User.class);
        when(userService.getUserByIdOrThrow(userId)).thenThrow(NoSuchUserException.class);

        assertThrows(
                NoSuchUserException.class,
                ()-> userProfileService.getUserProfileById(userId)
        );

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(userMapper, times(0)).userToUserResponseDTO(any(User.class));
    }

    @Test
    void updateEmailUser_Success_WhenUserExists() {
        long userId = 1L;
        String oldEmail = "oldEmail@email.com";
        String newEmail = "newEmail@email.com";
        User user = new User();
        user.setEmail(oldEmail);

        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        userProfileService.updateEmailUser(userId, newEmail);

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(kafkaProducerService, times(1)).sendEditEmailEvent(oldEmail, newEmail);
    }

    @Test
    void updateEmailUser_Failed_WhenUserNoExists() {
        long userId = 1L;
        String oldEmail = "oldEmail@email.com";
        String newEmail = "newEmail@email.com";

        when(userService.getUserByIdOrThrow(userId)).thenThrow(NoSuchUserException.class);
        assertThrows(
                NoSuchUserException.class,
                () -> userProfileService.updateEmailUser(userId, newEmail)
        );

        verify(userService, times(1)).getUserByIdOrThrow(userId);
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

        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(passwordEncoder.matches(user.getPassword(), oldPassword)).thenReturn(true);
        when(meterRegistry.counter("user_change_password")).thenReturn(counter);
        userProfileService.updatePasswordUser(userId, oldPassword, newPassword);

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(kafkaProducerService, times(1)).sendEditPasswordEvent(email);
    }

    @Test
    void updatePasswordUser_ThrowException_WhenUserNoExists() {
        long userId = 1L;
        String oldPassword = "000000";
        String newPassword = "0000000";
        String email = "email@email.com";

        when(userService.getUserByIdOrThrow(userId)).thenThrow(NoSuchUserException.class);
        assertThrows(
                NoSuchUserException.class,
                () -> userProfileService.updatePasswordUser(userId, oldPassword, newPassword)
        );

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(kafkaProducerService, never()).sendEditPasswordEvent(email);
    }

    @Test
    void updatePasswordUser_ThrowException_WhenPasswordsDifferent() {
        long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "differentOldPassword";
        String email = "email@email.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword(oldPassword);

        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(passwordEncoder.matches(user.getPassword(), "differentOldPassword"))
                .thenReturn(false);
        assertThrows(
                DifferentPasswordsException.class,
                () -> userProfileService.updatePasswordUser(
                        userId,
                        "differentOldPassword",
                        newPassword
                )
        );

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(kafkaProducerService, never()).sendEditPasswordEvent(email);
    }
}