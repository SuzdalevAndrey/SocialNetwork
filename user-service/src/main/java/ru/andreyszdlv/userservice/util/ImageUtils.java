package ru.andreyszdlv.userservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public final class ImageUtils {

    public static String generateImageId(MultipartFile file) {
        log.info("Executing generateFileName");

        String extension = getExtension(file);

        return UUID.randomUUID() + "." + extension;
    }

    public static boolean isImageFile(MultipartFile file) {
        log.info("Executing isImageFile");

        log.info("Checking file is image");
        return isImage(file) && hasImageExtension(file);
    }

    private static boolean hasImageExtension(MultipartFile file) {
        log.info("Executing hasImageExtension");

        String fileName = file.getOriginalFilename();

        log.info("Checking if image extension is {}", fileName);
        return fileName != null && fileName.matches(".*\\.(jpg|jpeg|png|gif|bmp)$");
    }

    private static boolean isImage(MultipartFile file) {
        log.info("Executing isImage");

        String contentType = file.getContentType();

        log.info("Checking if image type is {}", contentType);
        return contentType != null && contentType.startsWith("image/");
    }

    private static String getExtension(MultipartFile file) {
        log.info("Executing getExtension");

        return file.getOriginalFilename()
                .substring(file.getOriginalFilename()
                        .lastIndexOf(".") + 1);
    }
}