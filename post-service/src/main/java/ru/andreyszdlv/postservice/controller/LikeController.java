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
import ru.andreyszdlv.postservice.service.LocalizationService;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;

    private final LocalizationService localizationService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> createLike(@PathVariable long postId,
                                             @RequestHeader("X-User-Id") long userId,
                                             Locale locale){
        log.info("Executing createLike for postId: {}", postId);

        likeService.createLike(userId, postId);
        log.info("Successful create like with postId: {}", postId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                            "message.ok.create_like",
                            locale
                        )
                );
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> deleteLike(@PathVariable long postId,
                                             @RequestHeader("X-User-Id") long userId){
        log.info("Executing deleteLike for postId: {}", postId);

        likeService.deleteLike(userId, postId);
        log.info("Successful delete like with postId: {}", postId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
