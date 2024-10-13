package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.exception.AlreadyLikedException;
import ru.andreyszdlv.postservice.exception.NoLikedPostThisUserException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Like;
import ru.andreyszdlv.postservice.repository.LikeRepo;
import ru.andreyszdlv.postservice.repository.PostRepo;

@Slf4j
@Service
@AllArgsConstructor
public class LikeService {

    private final LikeRepo likeRepository;

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void createLike(long postId, String userEmail){
        log.info("Executing createLike method for postId: {}", postId);

        if(!postRepository.existsById(postId)) {
            log.error("Post no exists with postId: {}", postId);
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        log.info("Getting a userId by email");
        long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();
        log.info("Successful get userId by email");

        if(likeRepository.existsByPostIdAndUserId(postId, userId)){
            log.error("A user with userId: {} already liked post with postId: {}", userId, postId);
            throw new AlreadyLikedException("errors.409.already_liked_post");
        }

        Like like = new Like();

        like.setUserId(userId);
        like.setPostId(postId);

        log.info("Saving a like post with postId: {} and userId: {}", postId, userId);
        likeRepository.save(like);

        log.info("Successful save a like post with postId: {} and userId: {}", postId, userId);

        Long userIdAuthorPost = postRepository
                .findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                ).getUserId();

        String email = userServiceFeignClient.getUserEmailByUserId(userIdAuthorPost).getBody();
        String nameAuthorLike = userServiceFeignClient.getNameByUserEmail(userEmail).getBody();

        kafkaProducerService.sendCreateLikeEvent(
                email,
                nameAuthorLike
        );
    }

    @Transactional
    public void deleteLike(long postId, String userEmail) {
        log.info("Executing deleteLike method for postId: {}", postId);

        if(!postRepository.existsById(postId)) {
            log.error("Post no exists with postId: {}", postId);
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        log.info("Getting a userId by email");
        long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();
        log.info("Successful get userId by email");

        log.info("Deleting a like post with postId: {} and userId: {}", postId, userId);
        if (likeRepository.deleteByPostIdAndUserId(postId, userId) == 0) {

            log.error("The user with userId: {} not like post with postId: {}", userId, postId);
            throw new NoLikedPostThisUserException("errors.404.no_liked_post");
        }

        log.info("Successful delete a like post with postId: {} and userId: {}", postId, userId);
    }
}
