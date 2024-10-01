package ru.andreyszdlv.postservice.controller.comment;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.controller.comment.dto.CreateCommentRequestDTO;
import ru.andreyszdlv.postservice.controller.comment.dto.UpdateCommentRequestDTO;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.service.CommentService;

@RestController
@RequestMapping("/api/posts/comment")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable long postId, @Valid @RequestBody CreateCommentRequestDTO request) {
        return ResponseEntity.ok().body(commentService.createComment(postId, request.content()));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable long commentId, @Valid @RequestBody UpdateCommentRequestDTO request){
        commentService.updateComment(commentId, request.content());
        return ResponseEntity.ok().body("Комментарий обновлён");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().body("Комментарий удалён");
    }

}
