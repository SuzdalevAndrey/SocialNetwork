package ru.andreyszdlv.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
public final class ImageUtils {

    private ImageUtils(){}

    public static String generateImageId(MultipartFile file) {
        log.info("Executing generateFileName");

        String extension = getExtension(file);

        return UUID.randomUUID() + "." + extension;
    }

    private static String getExtension(MultipartFile file) {
        log.info("Executing getExtension");

        return file.getOriginalFilename()
                .substring(file.getOriginalFilename()
                        .lastIndexOf(".") + 1);
    }
}