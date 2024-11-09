package ru.andreyszdlv.userservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile image);

    String getImageUrlByImageId(String imageId);

    void deleteImageById(String imageId);
}

