package ru.andreyszdlv.postservice.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.andreyszdlv.postservice.service.LikeService;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/posts/like")
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;

    private final MessageSource messageSource;

    @PostMapping("/{postId}")
    public ResponseEntity<String> createLike(@PathVariable long postId,
                                             @RequestHeader("X-User-Email") String userEmail,
                                             Locale locale){
        log.info("Executing createLike for postId: {}", postId);

        likeService.createLike(postId, userEmail);
        log.info("Successful create like with postId: {}", postId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                messageSource.getMessage(
                        "message.ok.create_like",
                        null,
                        "message.ok.create_like",
                        locale
                )
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteLike(@PathVariable long postId,
                                             @RequestHeader("X-User-Email") String userEmail){
        log.info("Executing deleteLike for postId: {}", postId);

        likeService.deleteLike(postId,userEmail);
        log.info("Successful delete like with postId: {}", postId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
