package ru.andreyszdlv.postservice.service;

import lombok.AllArgsConstructor;
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

    private final PostRepo postRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    public List<Post> getPostsByUserEmail() {
        Long userId = userServiceFeignClient.getUserIdByUserEmail().getBody();

        return postRepository.findAllByUserId(userId);
    }

    public Post createPost(String content) {

        Long userId = userServiceFeignClient.getUserIdByUserEmail().getBody();

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

    public Post getPostByPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(()->new NoSuchPostException("errors.404.post_not_found"));
    }
}
