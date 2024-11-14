package ru.andreyszdlv.postservice.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.andreyszdlv.postservice.exception.DeleteImageException;
import ru.andreyszdlv.postservice.exception.EmptyImageException;
import ru.andreyszdlv.postservice.exception.FileDeleteException;
import ru.andreyszdlv.postservice.exception.FileIsNotImageException;
import ru.andreyszdlv.postservice.exception.NoSuchImageException;
import ru.andreyszdlv.postservice.service.S3Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageServiceImplTest {

    @Mock
    S3Service s3Service;

    @InjectMocks
    ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImage_ReturnImageId_WhenImageIsValid(){
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String imageId = imageService.uploadImage(image);
        assertNotNull(imageId);
    }

    @Test
    void uploadImage_ThrowException_WhenImageIsEmpty() {
        MockMultipartFile emptyImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);

        assertThrows(
                EmptyImageException.class,
                () -> imageService.uploadImage(emptyImage)
        );
    }

    @Test
    void uploadImage_ThrowException_WhenFileIsNotImage() {
        MockMultipartFile notImageFile = new MockMultipartFile("file", "test.txt", "text/plain", "not an image".getBytes());

        assertThrows(
                FileIsNotImageException.class,
                () -> imageService.uploadImage(notImageFile)
        );
    }

    @Test
    void getImageUrlByImageId_ReturnUrl_WhenImageExists() {
        String imageId = "imageId";
        String url = "http://localhost:1231/image";
        when(s3Service.getFileUrlById(imageId)).thenReturn(url);

        String response = imageService.getImageUrlByImageId(imageId);

        assertNotNull(response);
        assertEquals(response, url);
        verify(s3Service, times(1)).getFileUrlById(imageId);
    }

    @Test
    void getImageUrlByImageId_ThrowException_WhenImageNotExist() {
        String imageId = "imageId";
        when(s3Service.getFileUrlById(imageId)).thenThrow(RuntimeException.class);

        assertThrows(
                NoSuchImageException.class,
                () -> imageService.getImageUrlByImageId(imageId)
        );
    }

    @Test
    @SneakyThrows
    void deleteImageById_DeleteImage_WhenImageExists() {
        String imageId = "imageId";

        imageService.deleteImageById(imageId);

        verify(s3Service, times(1)).deleteFileById(imageId);
    }

    @Test
    @SneakyThrows
    void deleteImageById_ThrowException_WhenDeleteFails() {
        String imageId = "imageId";
        doThrow(FileDeleteException.class).when(s3Service).deleteFileById(imageId);

        assertThrows(
                DeleteImageException.class,
                () -> imageService.deleteImageById(imageId)
        );
    }
}