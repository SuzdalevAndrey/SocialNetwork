package ru.andreyszdlv.imageservice.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andreyszdlv.imageservice.dto.controller.ImageResponseDTO;
import ru.andreyszdlv.imageservice.exception.CreateBucketException;
import ru.andreyszdlv.imageservice.exception.DeleteImageException;
import ru.andreyszdlv.imageservice.exception.EmptyImageFileException;
import ru.andreyszdlv.imageservice.exception.ImageInputStreamCreationException;
import ru.andreyszdlv.imageservice.exception.ImageUploadException;
import ru.andreyszdlv.imageservice.exception.NoSuchImageException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public String upload(MultipartFile file, String bucketName) {
        log.info("Executing save");
        try {
            log.info("Creating bucket");
            createBucket(bucketName);
        } catch (Exception e) {
            log.error("Error create bucket: {}", e.getMessage());
            throw new CreateBucketException("errors.500.image_upload_failed");
        }

        log.info("Checking the image for emptiness");
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.error("Error: image is empty");
            throw new EmptyImageFileException("errors.400.image_is_empty");
        }

        log.info("Generating filename");
        String fileName = generateFileName(file);

        InputStream inputStream;

        try {
            log.info("Getting input stream from file");
            inputStream = file.getInputStream();
        } catch (Exception e) {
            log.error("Error create input stream: {}", e.getMessage());
            throw new ImageInputStreamCreationException("errors.400.image_upload_failed");
        }

        saveImage(inputStream, fileName, bucketName);

        return fileName;
    }

    private void createBucket(String bucketName) throws Exception {
        log.info("Executing createBucket for bucketName: {}", bucketName);

        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        log.info("Checking bucket exists for {}", bucketName);
        if (!found) {
            log.info("Bucket not exists, creating bucket for name: {}", bucketName);
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        log.info("Executing generateFileName");

        log.info("Extracting an extension from a name");
        String extension = getExtension(file);

        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        log.info("Executing getExtension");

        return file.getOriginalFilename()
                .substring(file.getOriginalFilename()
                        .lastIndexOf(".") + 1);
    }

    private void saveImage(InputStream inputStream, String fileName, String bucketName){
        log.info("Executing saveImage for bucketName: {}", bucketName);

        try (inputStream) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .stream(inputStream, inputStream.available(), -1)
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            log.info("Image saved to MinIO");
        } catch (Exception e) {
            log.error("Error save image: {}", e.getMessage());
            throw new ImageUploadException("errors.500.image_upload_failed");
        }
    }

    public ImageResponseDTO getImage(String imageId, String bucketName) {
        log.info("Executing getImage for imageId: {} and bucketName: {}", imageId, bucketName);

        try {
            log.info("Getting image from minio for imageId: {} and bucketName: {}",
                    imageId,
                    bucketName
            );
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageId)
                            .build()
            );

            log.info("Convert stream in byte[]");
            byte[] content = IOUtils.toByteArray(inputStream);

            log.info("Creating contentType from idImage: {}", imageId);
            String contentType = Files.probeContentType(Paths.get(imageId));

            return new ImageResponseDTO(contentType, content);

        } catch (Exception e) {
            log.error("Error: get image failed");
            throw new NoSuchImageException("errors.404.image_not_found");
        }
    }

    public void deleteImage(String imageId, String bucketName) {
        log.info("Executing deleteImage for imageId: {} and bucketName: {}", imageId, bucketName);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(imageId)
                            .build()
            );
        }
        catch (Exception e){
            log.error("Error: delete image failed");
            throw new DeleteImageException("errors.500.image_delete_failed");
        }
    }
}
