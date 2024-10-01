package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.exception.AlreadyLikedException;
import ru.andreyszdlv.postservice.model.Like;
import ru.andreyszdlv.postservice.repository.LikeRepo;

@Service
@AllArgsConstructor
public class LikeService {

    private final LikeRepo likeRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    public void createLike(long postId){
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
        likeRepository.deleteByPostIdAndUserId(postId, userServiceFeignClient.getUserIdByUserEmail().getBody());
    }
}
