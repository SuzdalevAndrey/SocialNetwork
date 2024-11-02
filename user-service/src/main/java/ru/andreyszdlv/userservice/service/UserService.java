package ru.andreyszdlv.userservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.exception.DifferentPasswordsException;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.mapper.UserMapper;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    private final KafkaProducerService kafkaProducerService;

    private final PasswordEncoder passwordEncoder;

    private final MeterRegistry meterRegistry;

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfileById(long userId) {
        log.info("Executing getUserById for id: {}", userId);

        User user = getUserById(userId);

        return userMapper.userToUserResponseDTO(user);
    }

    @Transactional
    public void updateEmailUser(long userId, String newEmail) {

        log.info("Executing updateEmailUser: userId = {}, newEmail = {}",
                userId,
                newEmail);

        User user = getUserById(userId);

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

        User user = getUserById(userId);

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

//    @Transactional
//    public String uploadImage(long userId, ImageRequestDTO imageDTO) {
//        log.info("Executing uploadImage for userId: {}", userId);
//        User user = getUserById(userId);
//
//        log.info("Uploading image fro userId: {}", userId);
//        String idImage = imageServiceFeignClient.saveAvatarUser(imageDTO).getBody();
//
//        log.info("Setting id image for user: {}", userId);
//        user.setIdImage(idImage);
//        return idImage;
//    }

//    @Transactional(readOnly = true)
//    public ImageResponseDTO getMyAvatar(long userId) {
//        log.info("Executing getMyAvatar for userId: {}", userId);
//
//        User user = getUserById(userId);
//
//        log.info("Getting avatar for userId: {}", userId);
//        return imageServiceFeignClient.getUserAvatar(user.getIdImage()).getBody();
//    }
//
//    @Transactional(readOnly = true)
//    public ImageResponseDTO getAvatarByIdImage(String idImage) {
//        log.info("Executing getAvatar for idImage: {}", idImage);
//
//        log.info("Getting image for idImage: {}", idImage);
//        return imageServiceFeignClient.getUserAvatar(idImage).getBody();
//    }

    @Transactional(readOnly = true)
    public User getUserById(long id){
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
}
