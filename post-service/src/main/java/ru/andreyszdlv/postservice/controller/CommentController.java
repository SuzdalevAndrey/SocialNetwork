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
import ru.andreyszdlv.postservice.dto.controller.comment.CommentResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.comment.CreateCommentRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.comment.UpdateCommentRequestDTO;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.service.CommentService;
import ru.andreyszdlv.postservice.service.LocalizationService;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final LocalizationService localizationService;

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CreateCommentRequestDTO request,
                                                            @RequestHeader("X-User-Id") long userId,
                                                            @PathVariable long postId,
                                                            BindingResult bindingResult)
            throws BindException {
        log.info("Executing createComment for postId: {} and content request: {}",
                postId,
                request.content());

        if(bindingResult.hasErrors()){
            log.error("Validation errors during create comment: {}",
                    bindingResult.getAllErrors());
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, create comment");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(userId, postId, request.content()));
    }

    @PatchMapping("/comment/{commentId}")
    public ResponseEntity<String> updateComment(@Valid @RequestBody UpdateCommentRequestDTO request,
                                                @PathVariable long commentId,
                                                @RequestHeader("X-User-Id") long userId,
                                                Locale locale,
                                                BindingResult bindingResult)
            throws BindException {
        log.info("Executing updateComment for commentId: {} and content request: {}",
                commentId,
                request.content());

        if(bindingResult.hasErrors()){
            log.info("Validation errors during update comment: {}",
                    bindingResult.getAllErrors());
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, update comment");
        commentService.updateComment(userId, commentId, request.content());

        log.info("Successful update comment with new content: {}", request.content());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                            "message.ok.update_comment",
                            locale
                        )
                );
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId,
                                              @RequestHeader("X-User-Id") long userId) {
        log.info("Executing deleteComment for commentId: {}", commentId);

        commentService.deleteComment(userId, commentId);
        log.info("Successful delete comment with commentId: {}", commentId);

        return ResponseEntity.noContent().build();
    }

}
