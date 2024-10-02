package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    public List<Post> getPostsByUserEmail() {
        log.info("Executing getPostsByUserEmail method");

        log.info("Getting a userId by email");
        long userId = userServiceFeignClient.getUserIdByUserEmail().getBody();

        log.info("Successful get userId by email");

        log.info("Getting List<Post> with userId: {}", userId);
        List<Post> responseList = postRepository.findAllByUserId(userId);

        log.info("Successful get List<Post> with userId: {}", userId);
        return responseList;
    }

    public Post createPost(String content) {

        log.info("Executing createPost method for content: {}", content);

        log.info("Getting a userId by email");
        long userId = userServiceFeignClient.getUserIdByUserEmail().getBody();

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
    public void updatePost(long id, String content) {
        log.info("Executing updatePost method for postId: {}, content: {}", id,  content);

        log.info("Getting a post by postId: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Successful get post by postId: {}", id);

        post.setContent(content);

        log.info("Successful update post with postId: {}, content: {}", id, content);
    }

    public void deletePost(long id) {
        log.info("Executing deletePost method for postId: {}", id);

        postRepository.deleteById(id);

        log.info("Successful delete post with postId: {}", id);
    }

    @Transactional
    public Post getPostByPostId(long postId) {
        log.info("Executing getPostByPostId method for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new NoSuchPostException("errors.404.post_not_found")
                );

        log.info("Successful get post by postId: {}", postId);

        post.setNumberViews(post.getNumberViews() + 1);

        log.info("Successful getPostByPostId with postId: {}", postId);
        return post;
    }
}
