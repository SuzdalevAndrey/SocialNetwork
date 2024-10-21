package ru.andreyszdlv.postservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.exception.AnotherUserCreatePostException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.mapper.PostMapper;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private final PostRepo postRepository;

    private final PostMapper postMapper;

    private final MeterRegistry meterRegistry;

    @Transactional(readOnly = true)
    public List<PostResponseDTO> getPostsByUserId(long userId) {
        log.info("Executing getPostsByUserId for user id: {}", userId);

        log.info("Getting List<Post> for userId: {}", userId);
        return postRepository
                .findAllByUserId(userId)
                .stream()
                .map(postMapper::postToPostResponseDTO)
                .toList();
    }

    @Transactional
    public PostResponseDTO createPost(long userId, String content) {

        log.info("Executing createPost for content: {}", content);

        log.info("Creating new post for userId: {} and content: {}", userId, content);
        Post post = new Post();
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        post.setUserId(userId);

        Post responsePost = postRepository.save(post);

        log.info("Successful create post with content: {}", content);

        incrementMetrics(userId, post.getId());

        return postMapper.postToPostResponseDTO(responsePost);
    }

    @Transactional
    public void updatePost(long userId, long id, String content) {
        log.info("Executing updatePost for postId: {}, content: {}", id,  content);

        log.info("Getting a post by postId: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Checking this user with userId: {} create post", userId);
        if(!post.getUserId().equals(userId)) {
            log.error("This user with userId: {} no create post", userId);
            throw new AnotherUserCreatePostException("errors.409.another_user_post");
        }

        log.info("Check successful, this user create post");

        post.setContent(content);

        log.info("Successful update post with postId: {}, content: {}", id, content);
    }

    @Transactional
    public void deletePost(long userId, long postId) {
        log.info("Executing deletePost for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Checking this user with userId: {} create post", userId);
        if(!post.getUserId().equals(userId)) {
            log.error("This user with userId: {} no create post", userId);
            throw new AnotherUserCreatePostException("errors.409.another_user_post");
        }

        log.info("Deleting post with postId: {}", postId);
        postRepository.deleteById(postId);
    }

    @Transactional
    public PostResponseDTO getPostByPostId(long postId) {
        log.info("Executing getPostByPostId for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Update number views post with postId: {}", postId);
        post.setNumberViews(post.getNumberViews() + 1);

        return postMapper.postToPostResponseDTO(post);
    }

    private void incrementMetrics(long userId, long postId) {
        meterRegistry.counter("posts_per_user", List.of(Tag.of("id", String.valueOf(userId)))).increment();
        meterRegistry.counter("comments_per_post", List.of(Tag.of("post_id", String.valueOf(postId)))).increment();
        meterRegistry.counter("likes_per_post", List.of(Tag.of("post_id", String.valueOf(postId)))).increment();
    }
}
