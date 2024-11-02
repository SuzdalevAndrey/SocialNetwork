package ru.andreyszdlv.imageservice.service;

import ru.andreyszdlv.imageservice.dto.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.ImageResponseDTO;

public interface ImageUserService{

    String saveAvatar(long userId, ImageRequestDTO image);

    ImageResponseDTO getAvatar(long userId);
}
