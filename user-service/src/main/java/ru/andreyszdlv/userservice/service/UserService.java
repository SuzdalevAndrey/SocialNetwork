package ru.andreyszdlv.userservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.ImageIdResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageUrlResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.exception.FileIsNotImageException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.exception.UserAlreadyHaveAvatarException;
import ru.andreyszdlv.userservice.exception.UserNotHaveAvatarException;
import ru.andreyszdlv.userservice.mapper.UserMapper;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import ru.andreyszdlv.userservice.util.ImageUtils;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    private final KafkaProducerService kafkaProducerService;

    private final PasswordEncoder passwordEncoder;

    private final MeterRegistry meterRegistry;

    private final UserMapper userMapper;

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfileById(long userId) {
        log.info("Executing getUserById for id: {}", userId);

        User user = getUserByIdOrThrow(userId);

        return userMapper.userToUserResponseDTO(user);
    }

    @Transactional
    public void updateEmailUser(long userId, String newEmail) {

        log.info("Executing updateEmailUser: userId = {}, newEmail = {}",
                userId,
                newEmail);

        User user = getUserByIdOrThrow(userId);

        String oldEmail = user.getEmail();

        user.setEmail(newEmail);

        log.info("Send data oldEmail: {}, newEmail: {} in kafka for update email event",
                oldEmail,
                newEmail
        );
        kafkaProducerService.sendEditEmailEvent(oldEmail, newEmail);
    }

    @Transactional
    public void updatePasswordUser(long userId, String oldPassword, String newPassword) {

        log.info("Executing updatePasswordUser for userId: {}", userId);

        User user = getUserByIdOrThrow(userId);

        log.info("Checking password comparison for userId: {}", userId);
        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            log.info("Updating password for userId: {}", userId);
            user.setPassword(passwordEncoder.encode(newPassword));

            log.info("Send data email: {} in kafka for update password event", user.getEmail());
            kafkaProducerService.sendEditPasswordEvent(user.getEmail());

            meterRegistry.counter("user_change_password").increment();
        }
        else{

            log.error("User password and the received password do not match");

            throw new DifferentPasswordsException("errors.409.invalid_password");
        }
    }

    @Transactional(readOnly = true)
    public User getUserByIdOrThrow(long id){
        log.info("Executing getUserById");

        log.info("Getting user by id: {}", id);
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );
        return user;
    }

    @Transactional
    public User getUserByEmail(String email){
        log.info("Executing getUserByEmail");

        log.info("Getting user by email: {}", email);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );
        return user;
    }

    @Transactional
    public ImageIdResponseDTO uploadAvatar(long userId, ImageRequestDTO avatarDTO) {
        log.info("Executing uploadAvatar for userId: {}", userId);

        User user = this.getUserByIdOrThrow(userId);

        log.info("Checking user exists avatar for userId: {}", userId);
        if(checkUserHasAvatar(user)){
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

        User user = this.getUserByIdOrThrow(userId);

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

        User user = this.getUserByIdOrThrow(userId);

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

        User user = this.getUserByIdOrThrow(userId);

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
