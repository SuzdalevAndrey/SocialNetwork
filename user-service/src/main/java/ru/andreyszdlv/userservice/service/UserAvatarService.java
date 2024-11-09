package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.ImageIdResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageUrlResponseDTO;
import ru.andreyszdlv.userservice.exception.UserAlreadyHaveAvatarException;
import ru.andreyszdlv.userservice.exception.UserNotHaveAvatarException;
import ru.andreyszdlv.userservice.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAvatarService {

    private final ImageService imageService;

    private final UserService userService;

    @Transactional
    public ImageIdResponseDTO uploadAvatar(long userId, ImageRequestDTO avatarDTO) {
        log.info("Executing uploadAvatar for userId: {}", userId);

        User user = userService.getUserByIdOrThrow(userId);

        log.info("Checking user exists avatar for userId: {}", userId);
        if(this.checkUserHasAvatar(user)){
            log.error("User: {} already have avatar", userId);
            throw new UserAlreadyHaveAvatarException("errors.409.user_already_have_avatar");
        }

        log.info("Uploading avatar for userId: {}", userId);
        String avatarId = imageService.uploadImage(avatarDTO.image());

        log.info("Setting id avatar for user: {}", userId);
        user.setIdImage(avatarId);

        return new ImageIdResponseDTO(avatarId);
    }

    @Transactional
    public ImageIdResponseDTO updateAvatar(long userId, ImageRequestDTO avatarDTO) {
        log.info("Executing updateAvatar for userId: {}", userId);

        User user = userService.getUserByIdOrThrow(userId);

        log.info("Checking user exists avatar for userId: {}", userId);
        if (!this.checkUserHasAvatar(user)) {
            log.error("User: {} not have avatar", userId);
            throw new UserNotHaveAvatarException("errors.409.user_not_have_avatar");
        }

        log.info("Uploading avatar for userId: {}", userId);
        String newAvatarId = imageService.uploadImage(avatarDTO.image());

        String oldAvatarId = user.getIdImage();

        log.info("Setting id avatar for user: {}", userId);
        user.setIdImage(newAvatarId);

        imageService.deleteImageById(oldAvatarId);

        return new ImageIdResponseDTO(newAvatarId);
    }

    @Transactional
    public void deleteAvatarByUserId(long userId){
        log.info("Executing deleteAvatarByUserId for userId: {}", userId);

        User user = userService.getUserByIdOrThrow(userId);

        if(!this.checkUserHasAvatar(user)){
            log.error("User: {} not have avatar", userId);
            throw new UserNotHaveAvatarException("errors.409.user_not_have_avatar");
        }

        log.info("Deleting avatar for userId: {}", userId);
        imageService.deleteImageById(user.getIdImage());

        user.setIdImage(null);
    }

    @Transactional(readOnly = true)
    public ImageUrlResponseDTO getAvatarUrlByUserId(long userId) {
        log.info("Executing getAvatarUrlByUserId for userId: {}", userId);

        User user = userService.getUserByIdOrThrow(userId);

        log.info("Getting avatar url for userId: {}", userId);
        return new ImageUrlResponseDTO(imageService.getImageUrlByImageId(user.getIdImage()));
    }

    @Transactional(readOnly = true)
    public ImageUrlResponseDTO getAvatarUrlById(String avatarId) {
        log.info("Executing getAvatarUrlById for avatarId: {}", avatarId);

        log.info("Getting avatar url for avatarId: {}", avatarId);
        return new ImageUrlResponseDTO(imageService.getImageUrlByImageId(avatarId));
    }

    private boolean checkUserHasAvatar(User user) {
        log.info("Checking if user has an avatar for userId: {}", user.getId());
        return user.getIdImage() != null;
    }
}
