package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.mapper.UserMapper;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InternalUserServiceTest {
    @Mock
    UserRepo userRepository;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    InternalUserService internalUserService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUserEmailByUserId_ReturnedEmail_WhenUserExists(){
        long userId = 1L;
        String mockEmail = "test@gmail.com";
        User user = mock(User.class);

        when(user.getEmail()).thenReturn(mockEmail);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        String email = internalUserService.getUserEmailByUserId(userId);

        assertNotNull(email);
        assertEquals(mockEmail, email);
    }

    @Test
    public void getUserEmailByUserId_ThrowsException_WhenUserNotFound(){
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchUserException.class,
                () -> internalUserService.getUserEmailByUserId(userId)
        );
    }

    @Test
    public void getNameByUserId_ReturnedName_WhenUserExists(){
        long userId = 1L;
        String mockName = "test";
        User user = mock(User.class);

        when(user.getName()).thenReturn(mockName);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        String name = internalUserService.getNameByUserId(userId);

        assertNotNull(name);
        assertEquals(mockName, name);
    }

    @Test
    public void getNameByUserId_ThrowsException_WhenUserNotFound(){
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchUserException.class,
                ()->internalUserService.getNameByUserId(userId)
        );
    }

    @Test
    public void getUserDetailsByEmail_ReturnedUserDetails_WhenUserExists(){
        long userId = 1L;
        String name = "name";
        String email = "test@gmail.com";
        User user = mock(User.class);
        UserDetailsResponseDTO userDetails = UserDetailsResponseDTO
                .builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(userMapper.userToUserDetailsResponseDTO(user)).thenReturn(userDetails);
        UserDetailsResponseDTO result = internalUserService.getUserDetailsByEmail(email);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(email, result.email());
        assertEquals(name, result.name());
    }

    @Test
    public void getUserDetailsByEmail_ThrowsException_WhenUserNotFound(){
        String email = "test@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchUserException.class,
                ()->internalUserService.getUserDetailsByEmail(email)
        );
        verify(userRepository, times(1)).findByEmail(email);
        verify(userMapper, never()).userToUserDetailsResponseDTO(any());
    }

    @Test
    public void saveUser_Success_WhenDBConnection(){
        String name = "name";
        String email = "test@gmail.com";
        String password = "password";
        ERole role = ERole.USER;

        internalUserService.saveUser(name, email, password, role);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void saveUser_ThrowsException_WhenNoDBConnection(){
        String name = "name";
        String email = "test@gmail.com";
        String password = "password";
        ERole role = ERole.USER;

        doThrow(new RuntimeException("Database Error")).when(userRepository).save(any(User.class));
        internalUserService.saveUser(name, email, password, role);

        verify(userRepository, times(1)).save(any(User.class));
        verify(
                kafkaProducerService,
                times(1)
        ).sendFailureSaveUserEvent(name, email, password, role);
    }

    @Test
    public void existsUserByEmail_ReturnTrue_WhenUserExists(){
        String email = "test@gmail.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(internalUserService.existsUserByEmail(email));
    }

    @Test
    public void existsUserByEmail_ReturnFalse_WhenUserNotFound(){
        String email = "test@gmail.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertFalse(internalUserService.existsUserByEmail(email));
    }

    @Test
    public void getUserByUserEmail_ReturnUser_WhenUserExists(){
        String email = "test@gmail.com";
        String name = "name";
        UserResponseDTO mockUserDTO = UserResponseDTO
                .builder()
                .name(name)
                .email(email)
                .build();
        User user = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(userMapper.userToUserResponseDTO(user)).thenReturn(mockUserDTO);
        UserResponseDTO result = internalUserService.getUserByUserEmail(email);

        assertNotNull(result);
        assertEquals(email, result.email());
        assertEquals(name, result.name());
    }

    @Test
    public void getUserByUserEmail_ThrowsException_WhenUserNotFound(){
        String email = "test@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchUserException.class,
                ()->internalUserService.getUserByUserEmail(email)
        );
    }
}
