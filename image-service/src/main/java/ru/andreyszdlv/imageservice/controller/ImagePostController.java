package ru.andreyszdlv.imageservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.imageservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.controller.ImageResponseDTO;
import ru.andreyszdlv.imageservice.service.ImagePostService;

@RestController
@RequestMapping("/api/images/post")
@RequiredArgsConstructor
public class ImagePostController {

    private final ImagePostService imageService;

    @PostMapping
    public ResponseEntity<String> saveImageForPost(@Valid @ModelAttribute ImageRequestDTO requestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imageService.saveImage(0, requestDTO));
    }

    @GetMapping("/{idImage}")
    public ResponseEntity<ImageResponseDTO> getPostImage(@RequestHeader("X-User-Id") long userId) {

        ImageResponseDTO responseDTO = imageService.getImage(userId);

        return ResponseEntity
                .ok(responseDTO);
    }
}
