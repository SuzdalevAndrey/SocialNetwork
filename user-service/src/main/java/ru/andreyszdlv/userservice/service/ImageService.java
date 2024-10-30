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
import ru.andreyszdlv.userservice.exception.ImageUploadException;
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
        try {
            createBucket();
        } catch (Exception e) {
            log.error("Error creating bucket: {}", e.getMessage());
            throw new ImageUploadException("errors.400.image_upload_failed");
        }
        MultipartFile file = image.multipartFile();
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.error("Error file empty");
            throw new ImageUploadException("errors.400.image_is_empty");
        }
        String fileName = generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            log.error("Error create input stream: {}", e.getMessage());
            throw new ImageUploadException("errors.400.image_upload_failed");
        }
        try {
            saveImage(inputStream, fileName);
        }
        catch (Exception e){
            log.error("Error save image: {}", e.getMessage());
            throw new ImageUploadException("errors.400.image_upload_failed");
        }
        return fileName;
    }

    private void createBucket() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename()
                        .lastIndexOf(".") + 1);
    }

    private void saveImage(InputStream inputStream, String fileName)
            throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getBucket())
                .object(fileName)
                .build());
    }

    public ImageResponseDTO getImage(String idImage) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(idImage)
                            .build()
            );

            byte[] content = IOUtils.toByteArray(inputStream);

            String contentType = Files.probeContentType(Paths.get(idImage));

            return new ImageResponseDTO(contentType, content);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ImageUploadException("errors.400.image_upload_failed");
        }
    }
}
