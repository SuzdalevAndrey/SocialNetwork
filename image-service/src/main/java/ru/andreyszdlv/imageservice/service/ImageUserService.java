package ru.andreyszdlv.imageservice.service;

import ru.andreyszdlv.imageservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.controller.ImageResponseDTO;

public interface ImageUserService{

    String saveAvatar(long userId, ImageRequestDTO image);

    ImageResponseDTO getAvatar(long userId);

    void deleteAvatar(long userId);

    void deleteImage(String imageId);
}
