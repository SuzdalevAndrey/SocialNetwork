package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.exception.AnotherUserCreatePostException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.model.Post;
import ru.andreyszdlv.postservice.repository.PostRepo;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostValidationService {

    private final PostRepo postRepository;

    public Post getPostByIdOrThrow(Long postId) {
        log.info("Getting a post by postId: {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(
                        () -> new NoSuchPostException("errors.404.post_not_found")
                );
    }

    public void validateUserOwnership(Post post, Long userId) {
        log.info("Checking if user with userId: {} created the post", userId);
        if (!post.getUserId().equals(userId)) {
            log.error("User with userId: {} is not the creator of the post", userId);
            throw new AnotherUserCreatePostException("errors.409.another_user_post");
        }
        log.info("Check successful, this user create post");
    }
}
