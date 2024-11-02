package ru.andreyszdlv.imageservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.imageservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.imageservice.dto.IdImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.ImageResponseDTO;
import ru.andreyszdlv.imageservice.props.MinioProperties;
import ru.andreyszdlv.imageservice.service.ImageUserService;
import ru.andreyszdlv.imageservice.service.MinioService;


@Service
@Slf4j
public class ImageUserServiceImpl implements ImageUserService {

    private final MinioService minioService;

    private final String bucketName;

    private final UserServiceFeignClient userServiceFeignClient;

    ImageUserServiceImpl(
            MinioService minioService,
            MinioProperties minioProperties,
            UserServiceFeignClient userServiceFeignClient){
        this.minioService = minioService;
        this.bucketName = minioProperties.getBucketUserAvatar();
        this.userServiceFeignClient = userServiceFeignClient;
    }

    @Override
    public String saveAvatar(long userId, ImageRequestDTO image) {
        log.info("Executing saveImage for user: {}", userId);

        log.info("Saving image for user: {}", userId);
        String imageId = minioService.upload(
                image.file(),
                bucketName
        );

        log.info("Sending idImage: {} in user-service for user: {}", imageId, userId);
        userServiceFeignClient.saveUserIdImage(userId, new IdImageRequestDTO(imageId));

        return imageId;
    }

    @Override
    public ImageResponseDTO getAvatar(long userId) {
        log.info("Executing getImage for user: {}", userId);

        log.info("Retrieving imageId for user: {} from user-service", userId);
        String imageId = userServiceFeignClient.getIdImageByUserId(userId).getBody();

        log.info("Retrieving image by imageId: {}", imageId);
        return minioService.getImage(
                imageId,
                bucketName
        );
    }
}
