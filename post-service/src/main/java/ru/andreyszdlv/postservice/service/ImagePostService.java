package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.postservice.dto.ImageDTO;
import ru.andreyszdlv.postservice.dto.controller.post.AddImagePostRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.post.PostImageIdsResponseDTO;
import ru.andreyszdlv.postservice.exception.ImagePostCountException;
import ru.andreyszdlv.postservice.exception.PostNoSuchImageException;
import ru.andreyszdlv.postservice.mapper.PostMapper;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImagePostService {

    private final PostMapper postMapper;

    private final ImageService imageService;

    private final PostValidationService postValidationService;

    @Transactional
    public PostImageIdsResponseDTO getImagesByPostId(long postId) {
        Post post = postValidationService.getPostByIdOrThrow(postId);

        return postMapper.postToPostImageIdsResponseDTO(post);
    }

    @Transactional
    public PostImageIdsResponseDTO addImagesPostByPostId(long userId,
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

        return postMapper.postToPostImageIdsResponseDTO(post);
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

    public ImageDTO getImageById(String imageId) {
        return imageService.getImageById(imageId);
    }
}
