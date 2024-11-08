package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.postservice.exception.FileDeleteException;
import ru.andreyszdlv.postservice.exception.FileUploadException;
import ru.andreyszdlv.postservice.props.S3Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    private final S3Properties s3Properties;

    private final S3Presigner s3Presigner;

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

    public String getFileUrlById(String fileId) {
        String bucketName = s3Properties.getBucketImagePost();
        log.info("Executing getFileUrlById for bucketName: {} and fileId: {}",
                bucketName,
                fileId);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofMinutes(s3Properties.getExpirationUrlInMinutes()))
                .getObjectRequest(
                        req -> req.bucket(bucketName).key(fileId)
                )
                .build();

        log.info("Creating file url for bucketName: {} and fileId: {}", bucketName, fileId);
        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
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
