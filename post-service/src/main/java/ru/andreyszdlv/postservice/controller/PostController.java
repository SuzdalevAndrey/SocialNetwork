package ru.andreyszdlv.postservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.dto.controllerDto.post.CreatePostRequestDTO;
import ru.andreyszdlv.postservice.dto.controllerDto.post.UpdatePostRequestDTO;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.service.PostService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<List<Post>> getPostsByUserEmail(){
        log.info("Executing getPostsByUserEmail method");

        List<Post> responePosts = postService.getPostsByUserEmail();
        log.info("Successful getPostsByUserEmail method");

        return ResponseEntity.ok(responePosts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId){
        log.info("Executing getPostById method for postId: {}", postId);

        Post responsePost = postService.getPostByPostId(postId);
        log.info("Successful get post for postId: {}", postId);

        return ResponseEntity.ok(responsePost);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createPost(@Valid @RequestBody CreatePostRequestDTO request,
                                           BindingResult bindingResult)
            throws BindException {
        log.info("Executing createPost method for content: {}", request.content());

        if(bindingResult.hasErrors()){
            log.error("Validation errors occurred during create post: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException ex)
                throw ex;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, creating post with content: {}", request.content());
        postService.createPost(request.content());

        log.info("Post create completed successfully with content: {}", request.content());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable long id,
                                           @Valid @RequestBody UpdatePostRequestDTO request,
                                           BindingResult bindingResult)
            throws BindException {
        log.info("Executing updatePost method for postId: {} with newContent: {}",
                id,
                request.content());

        if(bindingResult.hasErrors()){
            log.error("Validation errors occurred during update post: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException ex)
                throw ex;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, updating post with postId: {}, newContent: {}",
                id,
                request.content());
        postService.updatePost(id, request.content());

        log.info("Post update completed successfully with postId: {}, newContent: {}",
                id,
                request.content());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id){

        log.info("Executing deletePost method for postId: {}", id);
        postService.deletePost(id);

        log.info("Post delete completed successfully with postId: {}", id);
        return ResponseEntity.ok().build();
    }
}