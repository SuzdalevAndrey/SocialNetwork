package ru.andreyszdlv.postservice.controller.like;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.andreyszdlv.postservice.service.LikeService;

@RestController
@RequestMapping("/api/posts/like")
@AllArgsConstructor
public class LikeController {
    private static final Logger log = LoggerFactory.getLogger(LikeController.class);

    private final LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> createLike(@PathVariable long postId){
        log.info("Executing createLike method for postId: {}", postId);

        likeService.createLike(postId);
        log.info("Successful create like with postId: {}", postId);

        return ResponseEntity.ok("Лайк успешно поставлен");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deleteLike(@PathVariable long postId){
        log.info("Executing deleteLike method for postId: {}", postId);

        likeService.deleteLike(postId);
        log.info("Successful delete like with postId: {}", postId);

        return ResponseEntity.ok("Лайк успешно убран");
    }

}
