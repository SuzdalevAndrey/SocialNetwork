package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
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

    public void createComment(long postId, String content){

        if(!postRepository.existsById(postId)){
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        Comment comment = new Comment();

        comment.setUserId(userServiceFeignClient.getUserIdByUserEmail().getBody());
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setDateCreate(LocalDateTime.now());

        commentRepository.save(comment);
    }

}
