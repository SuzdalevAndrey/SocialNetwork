package ru.andreyszdlv.postservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class ImagePostController {

    private final ImagePostService imagePostService;

    @GetMapping("/{postId}/images")
    public ResponseEntity<List<PostImageUrlResponseDTO>> getImageByPostId(@PathVariable int postId) {
        return ResponseEntity.ok(imagePostService.getPostImageUrlsByPostId(postId));
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<PostImageUrlResponseDTO> getImageById(@PathVariable String imageId) {

        return ResponseEntity.ok(imagePostService.getPostImageUrlByImageId(imageId));
    }

    @PostMapping("/{postId}/images")
    public ResponseEntity<List<PostImageUrlResponseDTO>> addImagesPostByPostId(
            @RequestHeader("X-User-Id") long userId,
            @PathVariable long postId,
            @ModelAttribute AddImagePostRequestDTO imagesDTO
    ) {
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
        imagePostService.deleteImagePostByImageId(userId, postId, imageId);
        return ResponseEntity.noContent().build();
    }
}
