package ru.andreyszdlv.postservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.dto.controller.post.CreatePostRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.post.UpdatePostRequestDTO;
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

    private final ImageService imageService;

    private final PostValidationService postValidationService;

    @Transactional(readOnly = true)
    public List<PostResponseDTO> getPostsByUserId(long userId) {
        log.info("Executing getPostsByUserId for user id: {}", userId);

        log.info("Getting List<Post> for userId: {}", userId);
        return postMapper.listPostToListPostResponseDTO(
                postRepository.findAllByUserId(userId)
        );
    }

    @Transactional
    public PostResponseDTO createPost(long userId, CreatePostRequestDTO postRequestDTO) {

        log.info("Executing createPost for content: {}", postRequestDTO.content());

        log.info("Creating new post for userId: {} and content: {}", userId, postRequestDTO.content());
        Post post = new Post();
        post.setContent(postRequestDTO.content());
        post.setDateCreate(LocalDateTime.now());
        post.setNumberViews(0L);
        post.setUserId(userId);

        Post responsePost = postRepository.save(post);

        log.info("Uploading images for post: {}", post.getId());
        if (postRequestDTO.images() != null) {
            log.info("Images no empty, size = {}", postRequestDTO.images().size());
            List<String> imagesId = postRequestDTO.images()
                    .parallelStream()
                    .map(imageService::uploadImage)
                    .toList();
            post.setImageIds(imagesId);
        }

        log.info("Successful create post with content: {}", postRequestDTO.content());

        incrementMetrics(userId, responsePost.getId());

        return postMapper.postToPostResponseDTO(responsePost);
    }

    @Transactional
    public PostResponseDTO updatePost(long userId, long postId, UpdatePostRequestDTO postRequestDTO) {
        log.info("Executing updatePost for postId: {}, content: {}",
                postId,
                postRequestDTO.content()
        );

        Post post = postValidationService.getPostByIdOrThrow(postId);

        postValidationService.validateUserOwnership(post, userId);

        post.setContent(postRequestDTO.content());

        List<String> oldImageIds = post.getImageIds();

        if (postRequestDTO.images() != null) {
            List<String> newImageIds = postRequestDTO
                    .images()
                    .parallelStream()
                    .map(imageService::uploadImage)
                    .toList();

            post.setImageIds(newImageIds);
        } else {
            post.setImageIds(List.of());
        }

        oldImageIds.parallelStream().forEach(imageService::deleteImageById);

        log.info("Successful update post with postId: {}, content: {}",
                postId,
                postRequestDTO.content()
        );
        return postMapper.postToPostResponseDTO(post);
    }

    @Transactional
    public void deletePost(long userId, long postId) {
        log.info("Executing deletePost for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postValidationService.getPostByIdOrThrow(postId);

        postValidationService.validateUserOwnership(post, userId);

        log.info("Deleting post with postId: {}", postId);
        postRepository.deleteById(postId);

        log.info("Deleting images for post with postId: {}", postId);
        post.getImageIds().forEach(imageService::deleteImageById);
    }

    @Transactional
    public PostResponseDTO getPostByPostId(long postId) {
        log.info("Executing getPostByPostId for postId: {}", postId);

        log.info("Getting a post by postId: {}", postId);
        Post post = postValidationService.getPostByIdOrThrow(postId);

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
