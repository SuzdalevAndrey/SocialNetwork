package ru.andreyszdlv.imageservice.service;

import ru.andreyszdlv.imageservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.controller.ImageResponseDTO;

public interface ImagePostService {

    String saveImage(long userId, ImageRequestDTO image);

    ImageResponseDTO getImage(long userId);

}
