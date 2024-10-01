package ru.andreyszdlv.postservice.controller.comment;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.postservice.controller.comment.dto.CreateCommentRequestDTO;
import ru.andreyszdlv.postservice.service.CommentService;

@RestController
@RequestMapping("/api/posts/comment")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> createComment(@PathVariable long postId, @Valid @RequestBody CreateCommentRequestDTO request) {
        commentService.createComment(postId, request.content());
        return ResponseEntity.ok().body("Коммент создан");
    }

}
