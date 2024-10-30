package ru.andreyszdlv.userservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controller.ImageResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UpdateEmailRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.UpdatePasswordRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.UserImageRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.service.FriendService;
import ru.andreyszdlv.userservice.service.ImageService;
import ru.andreyszdlv.userservice.service.InternalUserService;
import ru.andreyszdlv.userservice.service.LocalizationService;
import ru.andreyszdlv.userservice.service.TempFriendService;
import ru.andreyszdlv.userservice.service.UserService;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final LocalizationService localizationService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserById(@RequestHeader("X-User-Id") long userId){
        log.info("Executing getUserByEmail for email: {}", userId);

        log.info("Getting user by Id: {}", userId);
        UserResponseDTO userResponse = userService.getUserProfileById(userId);

        log.info("Successfully get user by id: {}", userId);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/edit-email")
    public ResponseEntity<String> updateEmailUser(@Valid @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO,
                                                  BindingResult bindingResult,
                                                  @RequestHeader("X-User-Id") long userId,
                                                  Locale locale)
            throws BindException {

        log.info("Executing updateEmailUser for userId = {}, newEmail = {}",
                userId,
                updateEmailRequestDTO.email()
        );

        if (bindingResult.hasErrors()) {
            log.error("Validation errors during email update: {}",
                    bindingResult.getAllErrors());
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, userId = {}, newEmail = {}",
                userId,
                updateEmailRequestDTO.email()
        );
        userService.updateEmailUser(userId, updateEmailRequestDTO.email());

        log.info("Email update completed successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                                "message.ok.update_email",
                                locale
                        )
                );
    }


    @PatchMapping("/change-password")
    public ResponseEntity<String> updatePasswordUser(@Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
                                                     BindingResult bindingResult,
                                                     @RequestHeader("X-User-Id") long userId,
                                                     Locale locale)
            throws BindException {

        log.info("Executing updatePasswordUser for userId: {}", userId);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors during password update: {}",
                    bindingResult.getAllErrors());
            if (bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, updating password for userId: {}", userId);
        userService.updatePasswordUser(userId, updatePasswordRequestDTO.oldPassword(), updatePasswordRequestDTO.newPassword());

        log.info("Password update completed successfully for userId: {}", userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                                "message.ok.change_password",
                                locale
                        )
                );
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadImage(@RequestHeader("X-User-Id") long userId,
                                              @Valid @ModelAttribute UserImageRequestDTO imageDTO) {
        log.info("Executing uploadImage for userId: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.uploadImage(userId, imageDTO));
    }

    @GetMapping("/my-avatar")
    public ResponseEntity<byte[]> getAvatar(@RequestHeader("X-User-Id") long userId){
        log.info("Executing getAvatar for userId: {}", userId);
        ImageResponseDTO responseDTO = userService.getMyAvatar(userId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(responseDTO.contentType()))
                .body(responseDTO.content());
    }

    @GetMapping("/avatar/{idImage}")
    public ResponseEntity<byte[]> getImage(@PathVariable String idImage){
        log.info("Executing getImage for idImage: {}", idImage);
        ImageResponseDTO responseDTO = userService.getAvatar(idImage);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(responseDTO.contentType()))
                .body(responseDTO.content());
    }
}
