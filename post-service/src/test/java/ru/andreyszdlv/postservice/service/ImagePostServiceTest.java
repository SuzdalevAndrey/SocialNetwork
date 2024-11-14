package ru.andreyszdlv.postservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.andreyszdlv.postservice.dto.controller.post.AddImagePostRequestDTO;
import ru.andreyszdlv.postservice.dto.controller.post.PostImageUrlResponseDTO;
import ru.andreyszdlv.postservice.exception.ImagePostCountException;
import ru.andreyszdlv.postservice.exception.NoSuchPostException;
import ru.andreyszdlv.postservice.exception.PostNoSuchImageException;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImagePostServiceTest {

    @Mock
    ImageService imageService;

    @Mock
    PostValidationService postValidationService;

    @InjectMocks
    ImagePostService imagePostService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPostImageUrlsByPostId_ReturnsListImageUrl_WhenPostExistAndPostHasImage() {
        long postId = 1L;
        String imageId1 = "imageId1";
        String imageId2 = "imageId2";
        String url1 = "http://localhost:3243/imageId1";
        String url2 = "http://localhost:3243/imageId2";
        Post post = new Post();
        post.setImageIds(List.of(imageId1, imageId2));
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);
        when(imageService.getImageUrlByImageId(anyString()))
                .thenAnswer(
                        invocation -> "http://localhost:3243/" + invocation.getArgument(0)
                );

        List<PostImageUrlResponseDTO> response = imagePostService.getPostImageUrlsByPostId(postId);

        assertEquals(url1, response.get(0).url());
        assertEquals(url2, response.get(1).url());
        assertEquals(2, response.size());
    }

    @Test
    void getPostImageUrlsByPostId_ReturnsEmptyListImageUrl_WhenPostExistAndPostNotHasImage() {
        long postId = 1L;
        Post post = new Post();
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);
        when(imageService.getImageUrlByImageId(anyString()))
                .thenAnswer(
                        invocation -> "http://localhost:3243/" + invocation.getArgument(0)
                );

        List<PostImageUrlResponseDTO> response = imagePostService.getPostImageUrlsByPostId(postId);

        assertTrue(response.isEmpty());
    }

    @Test
    void getPostImageUrlsByPostId_ThrowsException_WhenPostNotExist() {
        long postId = 1L;
        when(postValidationService.getPostByIdOrThrow(postId)).thenThrow(NoSuchPostException.class);

        assertThrows(
                NoSuchPostException.class,
                () -> imagePostService.getPostImageUrlsByPostId(postId)
        );

        verify(postValidationService, times(1)).getPostByIdOrThrow(postId);
    }

    @Test
    void addImagesPostByPostId_ReturnsListImageUrl_WhenAddedSuccess() {
        long postId = 1L;
        long userId = 2L;
        String imageId1 = "imageId1";
        String imageId2 = "imageId2";
        MockMultipartFile image3 = new MockMultipartFile(
                "image3",
                "avatar.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );
        MockMultipartFile image4 = new MockMultipartFile(
                "image4",
                "avatar.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );
        String url1 = "http://localhost:2432/imageId1";
        String url2 = "http://localhost:2432/imageId2";
        String url3 = "http://localhost:2432/imageId3";
        String url4 = "http://localhost:2432/imageId4";
        AddImagePostRequestDTO addImagePostRequestDTO =
                new AddImagePostRequestDTO(List.of(image3, image4));
        Post post = new Post();
        post.setImageIds(List.of(imageId1, imageId2));
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);
        when(imageService.uploadImage(image3)).thenReturn("imageId3");
        when(imageService.uploadImage(image4)).thenReturn("imageId4");
        when(imageService.getImageUrlByImageId(anyString()))
                .thenAnswer(
                        invocation -> "http://localhost:2432/" + invocation.getArgument(0)
                );

        List<PostImageUrlResponseDTO> response = imagePostService
                .addImagesPostByPostId(userId, postId, addImagePostRequestDTO);

        assertEquals(url1, response.get(0).url());
        assertEquals(url2, response.get(1).url());
        assertEquals(url3, response.get(2).url());
        assertEquals(url4, response.get(3).url());
    }

    @Test
    void addImagesPostByPostId_ThrowException_CountImageMore10() {
        long postId = 1L;
        long userId = 2L;
        MockMultipartFile image3 = new MockMultipartFile(
                "image3",
                "avatar.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );
        MockMultipartFile image4 = new MockMultipartFile(
                "image4",
                "avatar.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );
        AddImagePostRequestDTO addImagePostRequestDTO =
                new AddImagePostRequestDTO(List.of(image3, image4));
        Post post = new Post();
        post.setImageIds(
                List.of("imageId1","imageId2","imageId3","imageId4","imageId5","imageId6","imageId7","imageId8","imageId9")
        );
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);

        assertThrows(
                ImagePostCountException.class,
                () -> imagePostService.addImagesPostByPostId(userId, postId, addImagePostRequestDTO)
        );
    }

    @Test
    void deleteImagePostByImageId_Success_PostContainsImage() {
        long userId = 1L;
        long postId = 2L;
        String imageId1 = "imageId1";
        String imageId2 = "imageId2";
        Post post = new Post();
        post.setUserId(userId);
        post.setImageIds(List.of(imageId1, imageId2));
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);

        imagePostService.deleteImagePostByImageId(userId, postId, imageId1);

        assertEquals(1, post.getImageIds().size());
        assertEquals(imageId2, post.getImageIds().get(0));
    }

    @Test
    void deleteImagePostByImageId_ThrowException_PostNoContainsImage() {
        long userId = 1L;
        long postId = 2L;
        String imageId1 = "imageId1";
        String imageId2 = "imageId2";
        String imageId3 = "imageId3";
        Post post = new Post();
        post.setUserId(userId);
        post.setImageIds(List.of(imageId1, imageId2));
        when(postValidationService.getPostByIdOrThrow(postId)).thenReturn(post);

        assertThrows(
                PostNoSuchImageException.class,
                () -> imagePostService.deleteImagePostByImageId(userId, postId, imageId3)
        );

        assertEquals(2, post.getImageIds().size());
        assertEquals(imageId1, post.getImageIds().get(0));
        assertEquals(imageId2, post.getImageIds().get(1));
    }

    @Test
    void getPostImageUrlByImageId_Success_WhenImageExists() {
        String imageId = "imageId";
        String url = "http://localhost:3913/" + imageId;
        when(imageService.getImageUrlByImageId(imageId)).thenReturn(url);

        PostImageUrlResponseDTO responseDTO = imagePostService.getPostImageUrlByImageId(imageId);

        assertNotNull(responseDTO);
        assertEquals(url, responseDTO.url());
    }
}
