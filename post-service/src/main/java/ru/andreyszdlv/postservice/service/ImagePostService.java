package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.dto.controller.post.PostImageUrlResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.post.AddImagePostRequestDTO;
import ru.andreyszdlv.postservice.exception.ImagePostCountException;
import ru.andreyszdlv.postservice.exception.PostNoSuchImageException;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImagePostService {

    private final ImageService imageService;

    private final PostValidationService postValidationService;

    @Transactional(readOnly = true)
    public List<PostImageUrlResponseDTO> getPostImageUrlsByPostId(long postId) {
        log.info("Executing getPostImageUrlsByPostId for postId: {}", postId);

        Post post = postValidationService.getPostByIdOrThrow(postId);

        log.info("Getting url for post image ids");
        return post.getImageIds().stream().map(this::getPostImageUrlByImageId).toList();
    }

    @Transactional
    public List<PostImageUrlResponseDTO> addImagesPostByPostId(long userId,
                                                               long postId,
                                                               AddImagePostRequestDTO imagesDTO) {
        log.info("Executing addImagesPostByPostId for postId: {}", postId);

        Post post = postValidationService.getPostByIdOrThrow(postId);

        postValidationService.validateUserOwnership(post, userId);

        log.info("Checking cur count image + request count image > 10");
        if (post.getImageIds().size() + imagesDTO.images().size() > 10) {
            log.error("Cur count image + request count image > 10");
            throw new ImagePostCountException("errors.409.image_count_invalid");
        }

        log.info("Cur count image + request count image <= 10");

        log.info("Uploading new image");
        List<String> newImageIds = imagesDTO
                .images()
                .parallelStream()
                .map(imageService::uploadImage)
                .toList();

        log.info("Adding new image ids to existing image ids");
        post.getImageIds().addAll(newImageIds);

        log.info("Getting url for post image ids");
        return post.getImageIds().parallelStream().map(this::getPostImageUrlByImageId).toList();
    }

    @Transactional
    public void deleteImagePostByImageId(long userId, long postId, String imageId) {
        log.info("Executing deleteImagePostByImageId for postId: {}", postId);
        Post post = postValidationService.getPostByIdOrThrow(postId);

        postValidationService.validateUserOwnership(post, userId);

        log.info("Checking post exist image: {}", imageId);
        if(!post.getImageIds().contains(imageId)){
            log.error("Post no exist image: {}", imageId);
            throw new PostNoSuchImageException("errors.404.post_image_not_found");
        }

        log.info("Post exists image, deleting imageId from list images post");
        post.getImageIds().remove(imageId);

        log.info("Deleting image");
        imageService.deleteImageById(imageId);
    }

    public PostImageUrlResponseDTO getPostImageUrlByImageId(String imageId) {
        log.info("Executing getPostImageUrlByImageId for imageId: {}", imageId);
        return new PostImageUrlResponseDTO(imageService.getImageUrlByImageId(imageId));
    }
}
