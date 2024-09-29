package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.postservice.execption.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    public List<Post> getPostsByUserEmail(String userEmail) {
        Long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();

        return postRepository.findAllByUserId(userId);
    }

    public Post createPost(String userEmail, String content) {

        Long userId = userServiceFeignClient.getUserIdByUserEmail(userEmail).getBody();

        Post post = new Post();
        post.setContent(content);
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        post.setUserId(userId);

        return postRepository.save(post);
    }

    @Transactional
    public void updatePost(long id, String content) {
        Post post = postRepository.findById(id)
                .orElseThrow(()->new NoSuchPostException("errors.404.post_not_found"));

        post.setContent(content);
    }

    public void deletePost(long id) {
        postRepository.deleteById(id);
    }
}
