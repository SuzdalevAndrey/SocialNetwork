package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.controller.PostController;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    private final Logger logger = LoggerFactory.getLogger(PostService.class);

    public List<Post> getPostsByUserEmail(String userEmail) {
        logger.info("getPostsByUserEmail");
        Long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();

        return postRepository.findAllByUserId(userId);
    }

    public Post createPost(String userEmail, String content) {
        logger.info("createPost");
        Long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();
        Post post = new Post();
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        post.setUserId(userId);
        return postRepository.save(post);
    }
}
