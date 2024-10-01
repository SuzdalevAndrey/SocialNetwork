package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.exception.AnotherUsersCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.repository.CommentRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepo commentRepository;

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    public Comment createComment(long postId, String content){

        if(!postRepository.existsById(postId)){
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        Comment comment = new Comment();

        comment.setUserId(userServiceFeignClient.getUserIdByUserEmail().getBody());
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public void deleteComment(long commentId) {

        if(!commentRepository.existsById(commentId)){
            throw new NoSuchCommentException("errors.404.comment_not_found");
        }

        if(!commentRepository.findById(commentId).get().getUserId().equals(userServiceFeignClient.getUserIdByUserEmail().getBody())){
            throw new AnotherUsersCommentException("errors.409.another_user_comment");
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void updateComment(long commentId, String content) {

        if(!commentRepository.existsById(commentId)){
            throw new NoSuchCommentException("errors.404.comment_not_found");
        }

        if(!commentRepository.findById(commentId).get().getUserId().equals(userServiceFeignClient.getUserIdByUserEmail().getBody())){
            throw new AnotherUsersCommentException("errors.409.another_user_comment");
        }

        Comment comment = commentRepository.findById(commentId).get();
        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());
    }
}
