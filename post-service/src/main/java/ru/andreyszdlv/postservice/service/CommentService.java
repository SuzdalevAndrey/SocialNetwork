package ru.andreyszdlv.postservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.exception.AnotherUsersCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchCommentException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Comment;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.CommentRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepo commentRepository;

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    private final KafkaProducerService kafkaProducerService;

    private final MeterRegistry meterRegistry;

    @Transactional
    public Comment createComment(long postId, String content, String userEmail){
        log.info("Executing createComment for postId: {}, content: {}, user email: {}",
                postId,
                content,
                userEmail);

        log.info("Checking exists post by id: {}", postId);
        if(!postRepository.existsById(postId)){
            log.error("Post no exists with postId: {}", postId);
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        Comment comment = new Comment();

        log.info("Getting a userId by email: {}", userEmail);
        comment.setUserId(userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody());
        log.info("Successful get userId by email");

        comment.setPostId(postId);
        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());

        log.info("Saving comment with postId: {}, content: {}", postId, content);
        Comment commentResponse = commentRepository.save(comment);

        log.info("Successful save comment with postId: {}, content: {}", postId, content);

        log.info("Getting a userId author post by postId");
        Long userIdAuthorPost = postRepository
                .findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                ).getUserId();
        log.info("Successful get userId: {} author post by postId", userIdAuthorPost);

        log.info("Getting email author post by author id: {}", userIdAuthorPost);
        String email = userServiceFeignClient.getUserEmailByUserId(userIdAuthorPost).getBody();

        log.info("Getting name author comment by email author comment: {}", userEmail);
        String nameAuthorComment = userServiceFeignClient.getNameByUserEmail(userEmail).getBody();

        log.info("Send data email: {}, nameAuthor: {}, content: {} in kafka for create comment event",
                email,
                nameAuthorComment,
                content
        );
        kafkaProducerService.sendCreateCommentEvent(
                email,
                nameAuthorComment,
                content
        );

        meterRegistry.counter(
                "comments_per_post",
                        List.of(Tag.of("post_id",String.valueOf(postId)))
                )
                .increment();
        return commentResponse;
    }

    @Transactional
    public void deleteComment(long commentId, String userEmail) {
        log.info("Executing deleteComment for commentId: {}", commentId);

        log.info("Getting a comment by id: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(
                        ()->new NoSuchCommentException("errors.404.comment_not_found")
                );

        log.info("Checking this user with email: {} create comment", userEmail);
        if(!userEmail.equals(
                userServiceFeignClient.getUserEmailByUserId(
                        comment.getUserId()
                ).getBody()
        )){
            log.error("Comment does not belong user");
            throw new AnotherUsersCommentException("errors.409.another_user_comment");
        }

        log.info("Deleting comment with commentId: {}", commentId);
        commentRepository.deleteById(commentId);

        log.info("Successful delete comment with commentId: {}", commentId);
    }

    @Transactional
    public void updateComment(long commentId, String content, String userEmail) {
        log.info("Executing updateComment for commentId: {} and newContent: {}", commentId, content);

        log.info("Checking exists comment by id: {}", commentId);
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(
                        ()->new NoSuchCommentException("errors.404.comment_not_found")
                );

        log.info("Checking this user create comment");
        if(!userEmail.equals(
                userServiceFeignClient.getUserEmailByUserId(
                        comment.getUserId()
                ).getBody()
        )){
            log.error("Comment does not belong user");
            throw new AnotherUsersCommentException("errors.409.another_user_comment");
        }

        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());

        log.info("Successful update comment with commentId: {} and newContent: {}",
                commentId,
                content
        );
    }
}
