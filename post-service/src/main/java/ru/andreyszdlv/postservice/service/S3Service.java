package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.exception.FileDeleteException;
import ru.andreyszdlv.postservice.exception.FileUploadException;
import ru.andreyszdlv.postservice.exception.NoSuchFileException;
import ru.andreyszdlv.postservice.props.S3Properties;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    private final S3Properties s3Properties;

    public void saveFile(byte[] bytes, String fileId) throws FileUploadException {
        String bucketName = s3Properties.getBucketImagePost();
        log.info("Executing saveFile for bucketName: {} and fileId: {}",
                bucketName,
                fileId);

        try {
            log.info("Saving file in s3 for bucketName: {} and fileId: {}",
                    bucketName,
                    fileId);
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileId)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
            log.info("File saved to s3");
        } catch (Exception e) {
            log.error("Error saving file: {}", e.getMessage());
            throw new FileUploadException();
        }
    }

    public byte[] getFileById(String fileId) throws NoSuchFileException {
        String bucketName = s3Properties.getBucketImagePost();
        log.info("Executing getFileById for bucketName: {} and fileId: {}",
                bucketName,
                fileId);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileId)
                .build();
        try {
            log.info("Getting file from s3 for bucketName: {} and fileId: {}",
                    bucketName,
                    fileId);
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

            log.info("Converting stream in byte[]");
            return IoUtils.toByteArray(response);
        } catch (IOException e) {
            log.error("Error getting file from S3: {}", e.getMessage());
            throw new NoSuchFileException();
        }
    }

    public void deleteFileById(String fileId) throws FileDeleteException {
        String bucketName = s3Properties.getBucketImagePost();
        log.info("Executing deleteFileById for bucketName: {} and fileId: {}",
                bucketName,
                fileId);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileId)
                .build();

        try {
            log.info("Deleting file for fileId: {}", fileId);
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new FileDeleteException();
        }
    }
}
