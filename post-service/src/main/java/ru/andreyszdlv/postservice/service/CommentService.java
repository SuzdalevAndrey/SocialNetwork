package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepo commentRepository;

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    private final KafkaProducerService kafkaProducerService;

    public Comment createComment(long postId, String content, String userEmail){
        log.info("Executing createComment method for postId: {}, content: {}", postId, content);

        if(!postRepository.existsById(postId)){
            log.error("Post no exists with postId: {}", postId);
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        Comment comment = new Comment();

        log.info("Getting a userId by email");
        comment.setUserId(userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody());
        log.info("Successful get userId by email");

        comment.setPostId(postId);
        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());

        log.info("Saving a comment with postId: {}, content: {}", postId, content);
        Comment commentResponse = commentRepository.save(comment);

        log.info("Successful save a comment with postId: {}, content: {}", postId, content);

        Long userIdAuthorPost = postRepository
                .findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                ).getUserId();

        String email = userServiceFeignClient.getUserEmailByUserId(userIdAuthorPost).getBody();
        String nameAuthorComment = userServiceFeignClient.getNameByUserEmail(userEmail).getBody();

        kafkaProducerService.sendCreateCommentEvent(
                email,
                nameAuthorComment,
                content
        );

        return commentResponse;
    }

    public void deleteComment(long commentId, String userEmail) {
        log.info("Executing deleteComment method for commentId: {}", commentId);

        if(!commentRepository.existsById(commentId)){
            log.error("Comment no exists with commentId: {}", commentId);
            throw new NoSuchCommentException("errors.404.comment_not_found");
        }

        if(!commentRepository
                .findById(commentId)
                .get()
                .getUserId()
                .equals(userServiceFeignClient
                        .getUserIdByUserEmail(userEmail)
                        .getBody()
                )
        ){
            log.error("The comment does not belong to the user");
            throw new AnotherUsersCommentException("errors.409.another_user_comment");
        }

        log.info("Deleting a comment with commentId: {}", commentId);
        commentRepository.deleteById(commentId);

        log.info("Successful delete a comment with commentId: {}", commentId);

    }

    @Transactional
    public void updateComment(long commentId, String content, String userEmail) {
        log.info("Executing updateComment method for commentId: {} and newContent: {}", commentId, content);

        Comment comment;

        if((comment = commentRepository.findById(commentId).orElse(null)) == null){
            log.error("Comment no exists with commentId: {}", commentId);
            throw new NoSuchCommentException("errors.404.comment_not_found");
        }


        if(!comment.getUserId().equals(
                userServiceFeignClient
                .getUserIdByUserEmail(userEmail)
                .getBody())
        ){
            log.error("The comment does not belong to the user");
            throw new AnotherUsersCommentException("errors.409.another_user_comment");
        }

        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());

        log.info("Successful update a comment with commentId: {} and newContent: {}", commentId, content);
    }
}
