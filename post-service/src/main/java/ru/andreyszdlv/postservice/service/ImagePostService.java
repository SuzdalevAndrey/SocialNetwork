package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.dto.controller.post.PostImageUrlResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.post.AddImagePostRequestDTO;
import ru.andreyszdlv.postservice.exception.ImagePostCountException;
import ru.andreyszdlv.postservice.exception.PostNoSuchImageException;
import ru.andreyszdlv.postservice.mapper.PostMapper;
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
        Post post = postValidationService.getPostByIdOrThrow(postId);

        return post.getImageIds().stream().map(this::getPostImageUrlByImageId).toList();
    }

    @Transactional
    public List<PostImageUrlResponseDTO> addImagesPostByPostId(long userId,
                                                               long postId,
                                                               AddImagePostRequestDTO imagesDTO) {
        Post post = postValidationService.getPostByIdOrThrow(postId);

        postValidationService.validateUserOwnership(post, userId);

        if (post.getImageIds().size() + imagesDTO.images().size() > 10) {
            throw new ImagePostCountException("errors.409.image_count_invalid");
        }

        List<String> newImageIds = imagesDTO
                .images()
                .parallelStream()
                .map(imageService::uploadImage)
                .toList();

        post.getImageIds().addAll(newImageIds);

        return post.getImageIds().parallelStream().map(this::getPostImageUrlByImageId).toList();
    }

    @Transactional
    public void deleteImagePostByImageId(long userId, long postId, String imageId) {
        Post post = postValidationService.getPostByIdOrThrow(postId);

        postValidationService.validateUserOwnership(post, userId);

        if(!post.getImageIds().contains(imageId))
            throw new PostNoSuchImageException("errors.404.post_image_not_found");

        post.getImageIds().remove(imageId);

        imageService.deleteImageById(imageId);
    }

    public PostImageUrlResponseDTO getPostImageUrlByImageId(String imageId) {
        return new PostImageUrlResponseDTO(imageService.getImageUrlById(imageId));
    }
}
