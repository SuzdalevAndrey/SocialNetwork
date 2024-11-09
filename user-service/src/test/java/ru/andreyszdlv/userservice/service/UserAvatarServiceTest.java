package ru.andreyszdlv.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import ru.andreyszdlv.userservice.dto.controller.ImageIdResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageUrlResponseDTO;
import ru.andreyszdlv.userservice.exception.UserAlreadyHaveAvatarException;
import ru.andreyszdlv.userservice.exception.UserNotHaveAvatarException;
import ru.andreyszdlv.userservice.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAvatarServiceTest {

    @Mock
    ImageService imageService;

    @Mock
    UserService userService;

    @InjectMocks
    UserAvatarService userAvatarService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadAvatar_SuccessAndReturnAvatarId_WhenUserNotHasAvatar() {
        long userId = 1L;
        User user = mock(User.class);
        ImageRequestDTO imageRequestDTO = new ImageRequestDTO(mock(MultipartFile.class));
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(user.getIdImage()).thenReturn(null);
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn("imageId");

        ImageIdResponseDTO response = userAvatarService.uploadAvatar(userId, imageRequestDTO);

        assertNotNull(response);
        assertEquals(response.imageId(), "imageId");
        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(imageService, times(1)).uploadImage(any(MultipartFile.class));
    }

    @Test
    void uploadAvatar_ThrowException_WhenUserHasAvatar() {
        long userId = 1L;
        User user = mock(User.class);
        ImageRequestDTO imageRequestDTO = new ImageRequestDTO(mock(MultipartFile.class));
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(user.getIdImage()).thenReturn("imageId");

        assertThrows(
                UserAlreadyHaveAvatarException.class,
                () -> userAvatarService.uploadAvatar(userId, imageRequestDTO)
        );

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(imageService, never()).uploadImage(any(MultipartFile.class));
    }

    @Test
    void updateAvatar_SuccessAndReturnAvatarId_WhenUserHasAvatar() {
        long userId = 1L;
        String oldImageId = "oldImageId";
        String newImageId = "newImageId";
        User user = mock(User.class);
        ImageRequestDTO imageRequestDTO = new ImageRequestDTO(mock(MultipartFile.class));
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(user.getIdImage()).thenReturn(oldImageId);
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(newImageId);

        ImageIdResponseDTO response = userAvatarService.updateAvatar(userId, imageRequestDTO);

        assertNotNull(response);
        assertEquals(newImageId, response.imageId());
        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(imageService, times(1)).uploadImage(any(MultipartFile.class));
        verify(imageService, times(1)).deleteImageById(oldImageId);
    }

    @Test
    void updateAvatar_ThrowException_WhenUserNotHasAvatar() {
        long userId = 1L;
        User user = mock(User.class);
        ImageRequestDTO imageRequestDTO = new ImageRequestDTO(mock(MultipartFile.class));
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(user.getIdImage()).thenReturn(null);

        assertThrows(
                UserNotHaveAvatarException.class,
                () -> userAvatarService.updateAvatar(userId, imageRequestDTO)
        );

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(imageService, never()).uploadImage(any(MultipartFile.class));
        verify(imageService, never()).deleteImageById(any(String.class));
    }

    @Test
    void deleteAvatarByUserId_Success_WhenUserHasAvatar() {
        long userId = 1L;
        String imageId = "imageId";
        User user = mock(User.class);
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(user.getIdImage()).thenReturn(imageId);

        userAvatarService.deleteAvatarByUserId(userId);

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(imageService, times(1)).deleteImageById(imageId);
    }

    @Test
    void deleteAvatarByUserId_ThrowException_WhenUserNotHasAvatar() {
        long userId = 1L;
        User user = mock(User.class);
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(user.getIdImage()).thenReturn(null);

        assertThrows(
                UserNotHaveAvatarException.class,
                () -> userAvatarService.deleteAvatarByUserId(userId)
        );

        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(imageService, never()).deleteImageById(any(String.class));
    }

    @Test
    void getAvatarUrlByUserId_ReturnsAvatarUrl_WhenUserHasAvatar(){
        long userId = 1L;
        String url = "url";
        String avatarId = "avatarId";
        User user = mock(User.class);
        when(userService.getUserByIdOrThrow(userId)).thenReturn(user);
        when(user.getIdImage()).thenReturn(avatarId);
        when(imageService.getImageUrlByImageId(avatarId)).thenReturn(url);

        ImageUrlResponseDTO response = userAvatarService.getAvatarUrlByUserId(userId);

        assertNotNull(response);
        assertEquals(url, response.url());
        verify(userService, times(1)).getUserByIdOrThrow(userId);
        verify(imageService, times(1)).getImageUrlByImageId(avatarId);
    }

    @Test
    void getAvatarUrlById_ReturnsAvatarUrl_WhenUserHasAvatar(){
        String avatarId = "avatarId";
        String url = "url";
        when(imageService.getImageUrlByImageId(avatarId)).thenReturn(url);

        ImageUrlResponseDTO response = userAvatarService.getAvatarUrlById(avatarId);

        assertNotNull(response);
        assertEquals(url, response.url());
        verify(imageService, times(1)).getImageUrlByImageId(avatarId);
    }
}