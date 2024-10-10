package ru.andreyszdlv.postservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.dto.controllerDto.comment.CreateCommentRequestDTO;
import ru.andreyszdlv.postservice.dto.controllerDto.comment.UpdateCommentRequestDTO;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/api/posts/comment")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @RequestBody CreateCommentRequestDTO request,
                                                 @RequestHeader("X-User-Email") String userEmail) {
        log.info("Executing createComment method for postId: {} and content request: {}",
                request.postId(),
                request.content());

        Comment newComment = commentService.createComment(
                request.postId(),
                request.content(),
                userEmail
        );

        log.info("Successful comment creation with content: {}", request.content());

        return ResponseEntity.ok().body(newComment);
    }

    @PatchMapping
    public ResponseEntity<String> updateComment(@Valid @RequestBody UpdateCommentRequestDTO request,
                                                @RequestHeader("X-User-Email") String userEmail){
        log.info("Executing updateComment method for commentId: {} and content request: {}",
                request.commentId(),
                request.content());

        commentService.updateComment(request.commentId(), request.content(), userEmail);
        log.info("Successful update comment with new content: {}", request.content());

        return ResponseEntity.ok().body("Комментарий обновлён");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long commentId,
                                                @RequestHeader("X-User-Email") String userEmail) {
        log.info("Executing deleteComment method for commentId: {}", commentId);

        commentService.deleteComment(commentId, userEmail);
        log.info("Successful delete comment with commentId: {}", commentId);

        return ResponseEntity.ok().body("Комментарий удалён");
    }

}
