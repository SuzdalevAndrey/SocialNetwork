package ru.andreyszdlv.postservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.dto.controller.post.PostImageUrlResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.post.AddImagePostRequestDTO;
import ru.andreyszdlv.postservice.service.ImagePostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class ImagePostController {

    private final ImagePostService imagePostService;

    @GetMapping("/{postId}/images")
    public ResponseEntity<List<PostImageUrlResponseDTO>> getImagesByPostId(@PathVariable int postId) {
        log.info("Executing getImagesByPostId for postId: {}", postId);

        return ResponseEntity.ok(imagePostService.getPostImageUrlsByPostId(postId));
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<PostImageUrlResponseDTO> getImageByImageId(@PathVariable String imageId) {
        log.info("Executing getImageByImageId for imageId: {}", imageId);

        return ResponseEntity.ok(imagePostService.getPostImageUrlByImageId(imageId));
    }

    @PostMapping("/{postId}/images")
    public ResponseEntity<List<PostImageUrlResponseDTO>> addImagesPostByPostId(
            @RequestHeader("X-User-Id") long userId,
            @PathVariable long postId,
            @Valid @ModelAttribute AddImagePostRequestDTO imagesDTO,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Executing addImagesPostByPostId for postId: {}", postId);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors during add image to post: {}",
                    bindingResult.getAllErrors());

            if (bindingResult instanceof BindException ex)
                throw ex;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, add image to post: {}", postId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imagePostService.addImagesPostByPostId(userId, postId, imagesDTO));
    }

    @DeleteMapping("/{postId}/images/{imageId}")
    public ResponseEntity<Void> deleteImagePostByPostIdAndImageId(
            @RequestHeader("X-User-Id") long userId,
            @PathVariable long postId,
            @PathVariable String imageId
    ) {
        log.info("Executing deleteImagePostByPostIdAndImageId for postId: {}, and imageId: {}",
                postId,
                imageId
        );

        imagePostService.deleteImagePostByImageId(userId, postId, imageId);
        return ResponseEntity.noContent().build();
    }
}
