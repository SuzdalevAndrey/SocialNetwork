package ru.andreyszdlv.postservice.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.andreyszdlv.postservice.service.LikeService;

@Slf4j
@RestController
@RequestMapping("/api/posts/like")
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> createLike(@PathVariable long postId,
                                             @RequestHeader("X-User-Email") String userEmail){
        log.info("Executing createLike method for postId: {}", postId);

        likeService.createLike(postId, userEmail);
        log.info("Successful create like with postId: {}", postId);

        return ResponseEntity.ok("Лайк успешно поставлен");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deleteLike(@PathVariable long postId,
                                             @RequestHeader("X-User-Email") String userEmail){
        log.info("Executing deleteLike method for postId: {}", postId);

        likeService.deleteLike(postId,userEmail);
        log.info("Successful delete like with postId: {}", postId);

        return ResponseEntity.ok("Лайк успешно убран");
    }

}
