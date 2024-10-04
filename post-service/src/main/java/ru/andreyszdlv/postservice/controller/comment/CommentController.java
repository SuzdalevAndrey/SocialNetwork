package ru.andreyszdlv.postservice.controller.comment;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.controller.comment.dto.CreateCommentRequestDTO;
import ru.andreyszdlv.postservice.controller.comment.dto.UpdateCommentRequestDTO;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/api/posts/comment")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable long postId, @Valid @RequestBody CreateCommentRequestDTO request) {
        log.info("Executing createComment method for postId: {} and content request: {}", postId, request.content());

        Comment newComment = commentService.createComment(postId, request.content());
        log.info("Successful comment creation with content: {}", request.content());

        return ResponseEntity.ok().body(newComment);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable long commentId, @Valid @RequestBody UpdateCommentRequestDTO request){
        log.info("Executing updateComment method for commentId: {} and content request: {}", commentId, request.content());

        commentService.updateComment(commentId, request.content());
        log.info("Successful update comment with new content: {}", request.content());

        return ResponseEntity.ok().body("Комментарий обновлён");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long commentId) {
        log.info("Executing deleteComment method for commentId: {}", commentId);

        commentService.deleteComment(commentId);
        log.info("Successful delete comment with commentId: {}", commentId);

        return ResponseEntity.ok().body("Комментарий удалён");
    }

}
