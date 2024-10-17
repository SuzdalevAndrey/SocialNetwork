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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.dto.controller.comment.CreateCommentRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.comment.UpdateCommentRequestDTO;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.service.CommentService;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/posts/comment")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @RequestBody CreateCommentRequestDTO request,
                                                 BindingResult bindingResult,
                                                 @RequestHeader("X-User-Email") String userEmail)
            throws BindException {
        log.info("Executing createComment for postId: {} and content request: {}",
                request.postId(),
                request.content());

        if(bindingResult.hasErrors()){
            log.error("Validation errors during create comment: {}",
                    bindingResult.getAllErrors());
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, create comment");
        Comment newComment = commentService.createComment(
                request.postId(),
                request.content(),
                userEmail
        );

        log.info("Successful comment creation with content: {}", request.content());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newComment);
    }

    @PatchMapping
    public ResponseEntity<String> updateComment(@Valid @RequestBody UpdateCommentRequestDTO request,
                                                BindingResult bindingResult,
                                                @RequestHeader("X-User-Email") String userEmail,
                                                Locale locale)
            throws BindException {
        log.info("Executing updateComment for commentId: {} and content request: {}",
                request.commentId(),
                request.content());

        if(bindingResult.hasErrors()){
            log.info("Validation errors during update comment: {}",
                    bindingResult.getAllErrors());
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, update comment");
        commentService.updateComment(request.commentId(), request.content(), userEmail);

        log.info("Successful update comment with new content: {}", request.content());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    messageSource.getMessage(
                            "message.ok.update_comment",
                            null,
                            "message.ok.update_comment",
                            locale
                    )
                );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId,
                                              @RequestHeader("X-User-Email") String userEmail) {
        log.info("Executing deleteComment for commentId: {}", commentId);

        commentService.deleteComment(commentId, userEmail);
        log.info("Successful delete comment with commentId: {}", commentId);

        return ResponseEntity.noContent().build();
    }

}
