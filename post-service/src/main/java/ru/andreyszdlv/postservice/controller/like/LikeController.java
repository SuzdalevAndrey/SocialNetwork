package ru.andreyszdlv.postservice.controller.like;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

    private final LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> createLike(@PathVariable long postId){
        likeService.createLike(postId);
        return ResponseEntity.ok("Лайк успешно поставлен");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deleteLike(@PathVariable long postId){
        likeService.deleteLike(postId);
        return ResponseEntity.ok("Лайк успешно убран");
    }

}
