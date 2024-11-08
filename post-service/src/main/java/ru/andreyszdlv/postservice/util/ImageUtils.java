package ru.andreyszdlv.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public final class ImageUtils {

    //todo написать логи

    private ImageUtils(){}

    public static String generateImageId(MultipartFile file) {
        log.info("Executing generateFileName");

        String extension = getExtension(file);

        return UUID.randomUUID() + "." + extension;
    }

    public static boolean isImageFile(MultipartFile file) {
        if (!isImage(file) || !hasImageExtension(file)) {
            return false;
        }

        try {
            return ImageIO.read(file.getInputStream()) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean hasImageExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        return fileName != null && fileName.matches(".*\\.(jpg|jpeg|png|gif|bmp)$");
    }

    private static boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();

        return contentType != null && contentType.startsWith("image/");
    }

    private static String getExtension(MultipartFile file) {
        log.info("Executing getExtension");

        return file.getOriginalFilename()
                .substring(file.getOriginalFilename()
                        .lastIndexOf(".") + 1);
    }
}