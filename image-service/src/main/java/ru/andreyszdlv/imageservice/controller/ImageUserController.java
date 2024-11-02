package ru.andreyszdlv.imageservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.imageservice.dto.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.ImageResponseDTO;
import ru.andreyszdlv.imageservice.service.ImageUserService;

@RestController
@RequestMapping("/api/images/user/avatar")
@Slf4j
@RequiredArgsConstructor
public class ImageUserController {

    private final ImageUserService imageUserService;

    @PostMapping
    public ResponseEntity<String> saveAvatarUser(
            @RequestHeader("X-User-Id") long userId,
            @Valid @ModelAttribute ImageRequestDTO requestDTO){
        log.info("Executing saveAvatarUser for user: {}", userId);

        log.info("Saving avatar user: {}", userId);
        String imageId = imageUserService.saveAvatar(userId, requestDTO);

        log.info("Successfully saved avatar user: {} with imageId: {}", userId, imageId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imageId);
    }

    @GetMapping
    public ResponseEntity<byte[]> getMyAvatar(@RequestHeader("X-User-Id") long userId){
        log.info("Executing getMyAvatar for user: {}", userId);

        log.info("Getting avatar user: {}", userId);
        ImageResponseDTO responseDTO = imageUserService.getAvatar(userId);

        log.info("Successfully get avatar user: {}", userId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(responseDTO.contentType()))
                .body(responseDTO.content());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getAvatarByUserId(@PathVariable("userId") long userId){
        log.info("Executing getAvatarByUserId for user: {}", userId);

        log.info("Getting avatar user: {}", userId);
        ImageResponseDTO responseDTO = imageUserService.getAvatar(userId);

        log.info("Successfully get avatar user: {}", userId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(responseDTO.contentType()))
                .body(responseDTO.content());
    }
}
