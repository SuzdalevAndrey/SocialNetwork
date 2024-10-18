package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.exception.AnotherUserCreatePostException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    @Transactional(readOnly = true)
    public List<Post> getPostsByUserEmail(String userEmail) {
        log.info("Executing getPostsByUserEmail for user email: {}", userEmail);

        log.info("Getting a userId by email: {}", userEmail);
        long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();

        log.info("Getting List<Post> for userId: {}", userId);
        List<Post> responseList = postRepository.findAllByUserId(userId);

        log.info("Successful getPostsByUserEmail for user email: {}", userEmail);
        return responseList;
    }

    @Transactional
    public Post createPost(String userEmail, String content) {

        log.info("Executing createPost for content: {}", content);

        log.info("Getting a userId by email: {}", userEmail);
        long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();

        log.info("Creating new post for userId: {} and content: {}", userId, content);
        Post post = new Post();
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        post.setUserId(userId);

        log.info("Successful create post with content: {}", content);
        return postRepository.save(post);
    }

    @Transactional
    public void updatePost(String userEmail, long id, String content) {
        log.info("Executing updatePost for postId: {}, content: {}", id,  content);

        log.info("Getting a post by postId: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Checking this user with email: {} create post", userEmail);
        if(!userEmail.equals(
                userServiceFeignClient.getUserEmailByUserId(
                        post.getUserId()
                ).getBody()
        )) {
            log.error("This user with email: {} no create post", userEmail);
            throw new AnotherUserCreatePostException("errors.409.another_user_post");
        }

        log.info("Check successful, this user create post");

        post.setContent(content);

        log.info("Successful update post with postId: {}, content: {}", id, content);
    }

    @Transactional
    public void deletePost(String userEmail, long postId) {
        log.info("Executing deletePost for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Checking this user with email: {} create post", userEmail);
        if(!userEmail.equals(
                userServiceFeignClient.getUserEmailByUserId(
                        post.getUserId()
                ).getBody()
        )) {
            log.error("This user with email: {} no create post", userEmail);
            throw new AnotherUserCreatePostException("errors.409.another_user_post");
        }

        log.info("Deleting post with postId: {}", postId);
        postRepository.deleteById(postId);
    }

    @Transactional
    public Post getPostByPostId(long postId) {
        log.info("Executing getPostByPostId for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Update number views post with postId: {}", postId);
        post.setNumberViews(post.getNumberViews() + 1);

        return post;
    }
}
