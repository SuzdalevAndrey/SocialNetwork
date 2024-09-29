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

//    private final Logger logger = LoggerFactory.getLogger(PostService.class);

    public List<Post> getPostsByUserEmail(String userEmail) {
        ResponseEntity<?> response = userServiceFeignClient.getUserIdByUserEmail(userEmail);

        if(response.getStatusCode().is2xxSuccessful()){
            Long userId = (Long) response.getBody();

            return postRepository.findAllByUserId(userId);
        } else {
            throw new NoSuchElementException("errors.404.user_not_found");
        }
    }

    public Post createPost(String userEmail, String content) {

        ResponseEntity<?> response = userServiceFeignClient.getUserIdByUserEmail(userEmail);

        if(response.getStatusCode().is2xxSuccessful()){
            Long userId = (Long) response.getBody();
            Post post = new Post();
            post.setContent(content);
            post.setDateCreate(LocalDateTime.now());
            post.setNumberViews(0L);
            post.setUserId(userId);
            return postRepository.save(post);
        } else {
            throw new NoSuchElementException("errors.404.user_not_found");
        }
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
