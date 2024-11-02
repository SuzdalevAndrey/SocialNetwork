package ru.andreyszdlv.imageservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.imageservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.controller.ImageResponseDTO;
import ru.andreyszdlv.imageservice.props.MinioProperties;
import ru.andreyszdlv.imageservice.service.ImagePostService;
import ru.andreyszdlv.imageservice.service.MinioService;

@Service
@Slf4j
public class ImagePostServiceImpl implements ImagePostService {

    private final MinioService minioService;

    private final String bucketName;

    public ImagePostServiceImpl(MinioService minioService, MinioProperties minioProperties) {
        this.minioService = minioService;
        bucketName = minioProperties.getBucketPost();
    }

    public String saveImage(long userId, ImageRequestDTO image) {
        return minioService.upload(
                image.file(),
                bucketName
        );
    }

    public ImageResponseDTO getImage(long userId) {
        return minioService.getImage(
                "1132",
                bucketName
        );
    }
}

