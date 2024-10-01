package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.exception.AlreadyLikedException;
import ru.andreyszdlv.postservice.exception.NoLikedPostThisUserException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Like;
import ru.andreyszdlv.postservice.repository.LikeRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;

@Service
@AllArgsConstructor
public class LikeService {

    private final LikeRepo likeRepository;

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    public void createLike(long postId){
        if(!postRepository.existsById(postId)) {
            throw new NoSuchPostException("errors.404.post_not_found");
        }
        long userId = userServiceFeignClient.getUserIdByUserEmail().getBody();
        if(likeRepository.existsByPostIdAndUserId(postId, userId)){
            throw new AlreadyLikedException("errors.409.already_liked_post");
        }
        Like like = new Like();
        like.setUserId(userId);
        like.setPostId(postId);
        likeRepository.save(like);
    }

    public void deleteLike(long postId) {
        if(!postRepository.existsById(postId)) {
            throw new NoSuchPostException("errors.404.post_not_found");
        }
        long userId = userServiceFeignClient.getUserIdByUserEmail().getBody();
        if(!likeRepository.existsByPostIdAndUserId(postId, userId)){
            throw new NoLikedPostThisUserException("errors.404.no_liked_post");
        }
        likeRepository.deleteByPostIdAndUserId(postId, userServiceFeignClient.getUserIdByUserEmail().getBody());
    }
}
