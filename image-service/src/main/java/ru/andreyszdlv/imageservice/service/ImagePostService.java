package ru.andreyszdlv.imageservice.service;

import ru.andreyszdlv.imageservice.dto.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.ImageResponseDTO;

public interface ImagePostService {

    String saveImage(long userId, ImageRequestDTO image);

    ImageResponseDTO getImage(long userId);

}
