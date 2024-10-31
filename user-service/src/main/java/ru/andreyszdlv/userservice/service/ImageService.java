package ru.andreyszdlv.userservice.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andreyszdlv.userservice.dto.controller.ImageResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserImageRequestDTO;
import ru.andreyszdlv.userservice.exception.CreateBucketException;
import ru.andreyszdlv.userservice.exception.EmptyImageFileException;
import ru.andreyszdlv.userservice.exception.ImageInputStreamCreationException;
import ru.andreyszdlv.userservice.exception.ImageUploadException;
import ru.andreyszdlv.userservice.exception.NoSuchImageException;
import ru.andreyszdlv.userservice.props.MinioProperties;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    public String upload(UserImageRequestDTO image) {
        log.info("Executing upload");
        try {
            log.info("Create bucket");
            createBucket();
        } catch (Exception e) {
            log.error("Error creating bucket: {}", e.getMessage());
            throw new CreateBucketException("errors.500.image_upload_failed");
        }

        MultipartFile file = image.file();

        log.info("Checking the file for emptiness");
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.error("Error file empty");
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

        try {
            log.info("Saving image to minio");
            saveImage(inputStream, fileName);
        } catch (Exception e) {
            log.error("Error save image: {}", e.getMessage());
            throw new ImageUploadException("errors.500.image_upload_failed");
        }

        return fileName;
    }

    private void createBucket() throws Exception {
        log.info("Executing createBucket");

        log.info("Checking bucket exists");
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .build()
        );
        if (!found) {
            log.info("Bucket not exists, creating bucket");
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
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

    private void saveImage(InputStream inputStream, String fileName)
            throws Exception {
        log.info("Executing saveImage");
        minioClient.putObject(
                PutObjectArgs.builder()
                        .stream(inputStream, inputStream.available(), -1)
                        .bucket(minioProperties.getBucket())
                        .object(fileName)
                        .build()
        );
        log.info("Save image to minio");
    }

    public ImageResponseDTO getImage(String idImage) {
        log.info("Executing getImage");

        try {
            log.info("Getting image from minio");
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(idImage)
                            .build()
            );

            log.info("Convert stream in byte[]");
            byte[] content = IOUtils.toByteArray(inputStream);

            log.info("Creating contentType from idImage: {}", idImage);
            String contentType = Files.probeContentType(Paths.get(idImage));

            return new ImageResponseDTO(contentType, content);

        } catch (Exception e) {
            log.error("Get image failed");
            throw new NoSuchImageException("errors.404.image_not_found");
        }
    }
}
