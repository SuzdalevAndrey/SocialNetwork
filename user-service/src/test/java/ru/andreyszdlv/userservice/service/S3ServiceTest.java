package ru.andreyszdlv.userservice.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.userservice.exception.FileDeleteException;
import ru.andreyszdlv.userservice.exception.FileUploadException;
import ru.andreyszdlv.userservice.props.S3Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3ServiceTest {

    @Mock
    S3Properties s3Properties;

    @Mock
    S3Client s3Client;

    @Mock
    S3Presigner s3Presigner;

    @InjectMocks
    S3Service s3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    void saveFile_Success_WhenDataIsValidAndConnectedS3() {
        String bucketName = "bucketName";
        String fileId = "fileId";
        byte[] bytes = new byte[255];
        when(s3Properties.getBucketUserAvatar()).thenReturn(bucketName);

        s3Service.saveFile(bytes, fileId);

        verify(s3Properties, times(1)).getBucketUserAvatar();
        verify(s3Client, times(1)).putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class)
        );
    }

    @Test
    void saveFile_ThrowException_WhenDataInValidOrNoConnectedS3() {
        String bucketName = "bucketName";
        String fileId = "fileId";
        byte[] bytes = new byte[255];
        when(s3Properties.getBucketUserAvatar()).thenReturn(bucketName);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(
                FileUploadException.class,
                () -> s3Service.saveFile(bytes, fileId)
        );

        verify(s3Properties, times(1)).getBucketUserAvatar();
        verify(s3Client, times(1)).putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class)
        );
    }

    @Test
    @SneakyThrows
    void getFileUrlById_ReturnsUrl_WhenFileExists() {
        String fileId = "fileId";
        String urlString = "http://localhost:40232/api";
        String bucketName = "bucketName";
        URL url = new URL(urlString);
        PresignedGetObjectRequest objectRequest = mock(PresignedGetObjectRequest.class);
        when(s3Properties.getBucketUserAvatar()).thenReturn(bucketName);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(objectRequest);
        when(objectRequest.url()).thenReturn(url);

        String response = s3Service.getFileUrlById(fileId);

        assertEquals(urlString, response);
        verify(s3Presigner, times(1))
                .presignGetObject(any(GetObjectPresignRequest.class));
        verify(s3Properties, times(1)).getBucketUserAvatar();
    }

    @Test
    @SneakyThrows
    void deleteFileById_Success_WhenFileNotExistsOrNoConnectedS3() {
        String bucketName = "bucketName";
        String fileId = "fileId";
        when(s3Properties.getBucketUserAvatar()).thenReturn(bucketName);

        s3Service.deleteFileById(fileId);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verify(s3Properties, times(1)).getBucketUserAvatar();
    }

    @Test
    void deleteFileById_ThrowException_WhenFileExistsAndConnectedS3() {
        String bucketName = "bucketName";
        String fileId = "fileId";
        when(s3Properties.getBucketUserAvatar()).thenReturn(bucketName);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(RuntimeException.class);

        assertThrows(
                FileDeleteException.class,
                () -> s3Service.deleteFileById(fileId)
        );

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verify(s3Properties, times(1)).getBucketUserAvatar();
    }

}