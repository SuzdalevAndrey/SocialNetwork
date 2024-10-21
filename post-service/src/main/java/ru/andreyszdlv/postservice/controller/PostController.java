package ru.andreyszdlv.postservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
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
import ru.andreyszdlv.postservice.dto.controller.post.CreatePostRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.post.UpdatePostRequestDTO;
import ru.andreyszdlv.postservice.mapper.PostMapper;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.service.LocalizationService;
import ru.andreyszdlv.postservice.service.PostService;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    private final LocalizationService localizationService;

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getPostsByUserId(@RequestHeader("X-User-Id") long userId){

        log.info("Executing getPostsByUserId for userId: {}", userId);

        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long postId){
        log.info("Executing getPostById for postId: {}", postId);

        PostResponseDTO responsePost = postService.getPostByPostId(postId);
        log.info("Successful get post for postId: {}", postId);

        return ResponseEntity.ok(responsePost);
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody CreatePostRequestDTO request,
                                           BindingResult bindingResult,
                                           @RequestHeader("X-User-Id") long userId)
            throws BindException {
        log.info("Executing createPost for content: {}", request.content());

        if(bindingResult.hasErrors()){
            log.error("Validation errors during create post: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException ex)
                throw ex;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, creating post with content: {}", request.content());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        postService.createPost(userId, request.content())
                );
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updatePost(@Valid @RequestBody UpdatePostRequestDTO request,
                                           BindingResult bindingResult,
                                           @RequestHeader("X-User-Id") long userId,
                                           Locale locale)
            throws BindException {
        log.info("Executing updatePost for postId: {} with newContent: {}",
                request.postId(),
                request.content());

        if(bindingResult.hasErrors()){
            log.error("Validation errors during update post: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException ex)
                throw ex;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, updating post with postId: {}, newContent: {}",
                request.postId(),
                request.content());
        postService.updatePost(userId, request.postId(), request.content());

        log.info("Post update successfully with postId: {}, newContent: {}",
                request.postId(),
                request.content());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                                "message.ok.update_post",
                                locale
                        )
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id,
                                           @RequestHeader("X-User-Id") long userId){

        log.info("Executing deletePost for postId: {}", id);
        postService.deletePost(userId, id);

        log.info("Post delete successfully with postId: {}", id);
        return ResponseEntity.noContent().build();
    }
}
