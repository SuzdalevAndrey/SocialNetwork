package ru.andreyszdlv.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andreyszdlv.postservice.dto.ImageDTO;
import ru.andreyszdlv.postservice.exception.DeleteImageException;
import ru.andreyszdlv.postservice.exception.EmptyImageException;
import ru.andreyszdlv.postservice.exception.FileDeleteException;
import ru.andreyszdlv.postservice.exception.FileUploadException;
import ru.andreyszdlv.postservice.exception.ImageUploadException;
import ru.andreyszdlv.postservice.exception.NoSuchImageException;
import ru.andreyszdlv.postservice.util.ImageUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final S3Service s3Service;

    public String uploadImage(MultipartFile image) {
        log.info("Executing uploadImage");

        log.info("Validating image");
        validateImage(image);

        log.info("Saving new image");
        return this.saveNewImage(image);
    }

    public String updateImage(MultipartFile image, String deleteImageId) {
        log.info("Executing uploadImage");

        log.info("Validating image");
        validateImage(image);

        log.info("Saving new image");
        String imageId = this.saveNewImage(image);

        log.info("Deleting oldImage by id: {}", deleteImageId);
        this.deleteImageById(deleteImageId);

        return imageId;
    }

    public ImageDTO getImageById(String imageId) {
        log.info("Executing getImageById");

        try {
            log.info("Creating contentType for imageId: {}", imageId);
            String contentType = Files.probeContentType(Paths.get(imageId));

            log.info("Creating responseDTO for imageId: {}", imageId);
            ImageDTO image = ImageDTO.builder()
                    .contentType(contentType)
                    .content(s3Service.getFileById(imageId))
                    .build();

            return image;
        } catch (Exception e) {
            log.error("Error: no such image", e);
            throw new NoSuchImageException("errors.404.image_not_found");
        }
    }

    public void deleteImageById(String imageId) {
        log.info("Executing deleteImageById for imageId: {}", imageId);

        log.info("Deleting image for imageId: {}", imageId);
        try {
            s3Service.deleteFileById(imageId);
        }
        catch (FileDeleteException e){
            throw new DeleteImageException("errors.500.image_delete_failed");
        }
    }

    private void validateImage(MultipartFile avatar) {
        log.info("Checking the image for emptiness");
        if (avatar.isEmpty() || avatar.getOriginalFilename() == null) {
            log.error("Error: image is empty");
            throw new EmptyImageException("errors.400.image_is_empty");
        }
    }

    private String saveNewImage(MultipartFile avatar){
        log.info("Executing saveNewImage");

        log.info("Getting bytes from image");
        byte[] avatarBytes = this.getImageBytes(avatar);

        log.info("Generating imageId");
        String imageId = ImageUtils.generateImageId(avatar);

        try{
            log.info("Saving image for imageId: {}", imageId);
            s3Service.saveFile(avatarBytes, imageId);
        }
        catch(FileUploadException e){
            log.error("Error saving image: {}", e.getMessage());
            throw new ImageUploadException("errors.400.image_upload_failed");
        }

        return imageId;
    }

    private byte[] getImageBytes(MultipartFile avatar) {
        log.info("Executing getImageBytes");
        try {
            return avatar.getBytes();
        } catch (Exception e) {
            log.error("Error get byte[]: {}", e.getMessage());
            throw new ImageUploadException("errors.400.image_upload_failed");
        }
    }
}
