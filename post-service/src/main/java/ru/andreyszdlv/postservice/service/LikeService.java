package ru.andreyszdlv.postservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
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

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LikeService {

    private final LikeRepo likeRepository;

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    private final KafkaProducerService kafkaProducerService;

    private final MeterRegistry meterRegistry;

    @Transactional
    public void createLike(long userId, long postId){
        log.info("Executing createLike for postId: {}", postId);

        if(!postRepository.existsById(postId)) {
            log.error("Post no exists with postId: {}", postId);
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        if(likeRepository.existsByPostIdAndUserId(postId, userId)){
            log.error("User with userId: {} already liked post with postId: {}", userId, postId);
            throw new AlreadyLikedException("errors.409.already_liked_post");
        }

        Like like = new Like();

        like.setUserId(userId);
        like.setPostId(postId);

        log.info("Saving like post with postId: {} and userId: {}", postId, userId);
        likeRepository.save(like);

        log.info("Getting userId author post by postId: {}", postId);
        Long userIdAuthorPost = postRepository
                .findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                )
                .getUserId();

        log.info("Getting email author post by userId: {}", userIdAuthorPost);
        String email = userServiceFeignClient.getUserEmailByUserId(userIdAuthorPost).getBody();

        log.info("Getting name author like by userId: {}", userId);
        String nameAuthorLike = userServiceFeignClient.getNameByUserId(userId).getBody();

        log.info("Send data email: {}, nameAuthorLike: {} in kafka for create like event",
                email,
                nameAuthorLike
        );
        kafkaProducerService.sendCreateLikeEvent(
                email,
                nameAuthorLike
        );

        meterRegistry
                .counter(
                        "likes_per_post",
                        List.of(Tag.of("post_id", String.valueOf(postId)))
                )
                .increment();
    }

    @Transactional
    public void deleteLike(long userId, long postId) {
        log.info("Executing deleteLike for postId: {}", postId);

        if(!postRepository.existsById(postId)) {
            log.error("Post no exists with postId: {}", postId);
            throw new NoSuchPostException("errors.404.post_not_found");
        }

        log.info("Deleting like post with postId: {} and userId: {}", postId, userId);
        if (likeRepository.deleteByPostIdAndUserId(postId, userId) == 0) {
            log.error("User with userId: {} not like post with postId: {}", userId, postId);
            throw new NoLikedPostThisUserException("errors.404.no_liked_post");
        }

        log.info("Successful delete like post with postId: {} and userId: {}", postId, userId);
    }
}
