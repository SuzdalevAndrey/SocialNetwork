package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
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
        log.info("Executing getPostsByUserEmail method");

        log.info("Getting a userId by email");
        long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();

        log.info("Successful get userId by email");

        log.info("Getting List<Post> with userId: {}", userId);
        List<Post> responseList = postRepository.findAllByUserId(userId);

        log.info("Successful get List<Post> with userId: {}", userId);
        return responseList;
    }

    @Transactional
    public Post createPost(String content, String userEmail) {

        log.info("Executing createPost method for content: {}", content);

        log.info("Getting a userId by email");
        long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();

        log.info("Successful get userId: {} by email", userId);

        Post post = new Post();
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        post.setUserId(userId);

        log.info("Successful create post with content: {}", content);
        return postRepository.save(post);
    }

    @Transactional
    public void updatePost(long id, String content, String userEmail) {
        log.info("Executing updatePost method for postId: {}, content: {}", id,  content);

        log.info("Getting a post by postId: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );
        log.info("Successful get post by postId: {}", id);

        if(userEmail.equals(
                userServiceFeignClient.getUserEmailByUserId(
                        post.getUserId()
                ).getBody())
        ) {


            post.setContent(content);

            log.info("Successful update post with postId: {}, content: {}", id, content);
        }
        throw new RuntimeException();
    }

    @Transactional
    public void deletePost(long id, String userEmail) {
        log.info("Executing deletePost method for postId: {}", id);

        log.info("Getting a post by postId: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );
        log.info("Successful get post by postId: {}", id);

        if(userEmail.equals(
                userServiceFeignClient.getUserEmailByUserId(
                        post.getUserId()
                ).getBody())
        ) {

            postRepository.deleteById(id);

            log.info("Successful delete post with postId: {}", id);
        }

        throw new RuntimeException();
    }

    @Transactional(readOnly = true)
    public Post getPostByPostId(long postId) {
        log.info("Executing getPostByPostId method for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        post.setNumberViews(post.getNumberViews() + 1);

        log.info("Successful getPostByPostId with postId: {}", postId);
        return post;
    }
}
