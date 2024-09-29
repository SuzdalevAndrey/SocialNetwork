package ru.andreyszdlv.postservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.controller.dto.CreatePostRequestDTO;
import ru.andreyszdlv.postservice.controller.dto.UpdatePostRequestDTO;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    @GetMapping("/{useremail}")
    public ResponseEntity<List<Post>> getPostsByUserEmail(@PathVariable("useremail") String userEmail){
        logger.info("GetPostsByUserEmail: UserEmail = " + userEmail);
        return ResponseEntity.ok(postService.getPostsByUserEmail(userEmail));
    }

    @PostMapping("/create/{useremail}")
    public ResponseEntity<Void> createPost(@PathVariable("useremail") String userEmail,
                                           @Valid @RequestBody CreatePostRequestDTO request,
                                           BindingResult bindingResult)
            throws BindException {
        logger.info("CreatePost: UserEmail = " + userEmail);
        if(bindingResult.hasErrors()){
            if(bindingResult instanceof BindException ex)
                throw ex;
            throw new BindException(bindingResult);
        }
        postService.createPost(userEmail, request.content());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable long id,
                                           @Valid @RequestBody UpdatePostRequestDTO request,
                                           BindingResult bindingResult)
            throws BindException {
        if(bindingResult.hasErrors()){
            if(bindingResult instanceof BindException ex)
                throw ex;
            throw new BindException(bindingResult);
        }
        postService.updatePost(id, request.content());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id){
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
