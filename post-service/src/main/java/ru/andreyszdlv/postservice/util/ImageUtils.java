package ru.andreyszdlv.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public final class ImageUtils {

    private ImageUtils(){}

    public static String generateImageId(MultipartFile file) {
        log.info("Executing generateFileName");

        String extension = getExtension(file);

        return UUID.randomUUID() + "." + extension;
    }

    public static boolean isImageFile(MultipartFile file) {
        log.info("Executing isImageFile");

        log.info("Simple checking file is image");
        if (!isImage(file) || !hasImageExtension(file)) {
            log.error("File is not image");
            return false;
        }

        try {
            log.info("Detailed verification that file is image");
            return ImageIO.read(file.getInputStream()) != null;
        } catch (IOException e) {
            log.error("File is not image");
            return false;
        }
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