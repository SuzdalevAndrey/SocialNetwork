package ru.andreyszdlv.userservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controller.ImageIdResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.ImageResponseDTO;
import ru.andreyszdlv.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class ImageController {

    private final UserService userService;

    @PostMapping("/my-avatar")
    public ResponseEntity<ImageIdResponseDTO> uploadAvatar(@RequestHeader("X-User-Id") long userId,
                                                           @Valid @ModelAttribute ImageRequestDTO imageDTO,
                                                           BindingResult bindingResult)
            throws BindException {
        log.info("Executing uploadAvatar for userId: {}", userId);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors during upload avatar: {}",
                    bindingResult.getAllErrors());
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, uploading avatar for userId: {}", userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.uploadAvatar(userId, imageDTO));
    }

    @PatchMapping("/my-avatar")
    public ResponseEntity<ImageIdResponseDTO> updateAvatar(@RequestHeader("X-User-Id") long userId,
                                               @Valid @ModelAttribute ImageRequestDTO imageDTO,
                                               BindingResult bindingResult)
            throws BindException{
        log.info("Executing updateAvatar for userId: {}", userId);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors during update avatar: {}",
                    bindingResult.getAllErrors());
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, updating avatar for userId: {}", userId);
        return ResponseEntity.ok(userService.updateAvatar(userId, imageDTO));
    }


    @GetMapping("/my-avatar")
    public ResponseEntity<byte[]> getAvatarByUserId(@RequestHeader("X-User-Id") long userId){
        log.info("Executing getMyAvatar for userId: {}", userId);

        log.info("Getting user avatar for userId: {}", userId);
        ImageResponseDTO responseDTO = userService.getAvatarByUserId(userId);

        log.info("Successfully getMyAvatar for userId: {}", userId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(responseDTO.contentType()))
                .body(responseDTO.content());
    }

    @GetMapping("/avatar/{idImage}")
    public ResponseEntity<byte[]> getAvatarByIdImage(@PathVariable String idImage){
        log.info("Executing getAvatarByIdImage for idImage: {}", idImage);

        log.info("Getting user avatar for idImage: {}", idImage);
        ImageResponseDTO responseDTO = userService.getAvatarById(idImage);

        log.info("Successfully getAvatarByIdImage for idImage: {}", idImage);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(responseDTO.contentType()))
                .body(responseDTO.content());
    }

    @DeleteMapping("/my-avatar")
    public ResponseEntity<Void> deleteAvatarByUserId(@RequestHeader("X-User-Id") long userId){
        log.info("Executing deleteAvatarByUserId for userId: {}", userId);

        userService.deleteAvatarByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
