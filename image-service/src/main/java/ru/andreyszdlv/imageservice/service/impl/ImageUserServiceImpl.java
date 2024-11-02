package ru.andreyszdlv.imageservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.imageservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.imageservice.dto.controller.IdImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.controller.ImageRequestDTO;
import ru.andreyszdlv.imageservice.dto.controller.ImageResponseDTO;
import ru.andreyszdlv.imageservice.props.MinioProperties;
import ru.andreyszdlv.imageservice.service.ImageUserService;
import ru.andreyszdlv.imageservice.service.KafkaProducerService;
import ru.andreyszdlv.imageservice.service.MinioService;


@Service
@Slf4j
public class ImageUserServiceImpl implements ImageUserService {

    private final MinioService minioService;

    private final String bucketName;

    private final KafkaProducerService kafkaProducerService;

    private final UserServiceFeignClient userServiceFeignClient;

    ImageUserServiceImpl(
            MinioService minioService,
            MinioProperties minioProperties,
            UserServiceFeignClient userServiceFeignClient,
            KafkaProducerService kafkaProducerService){
        this.minioService = minioService;
        this.bucketName = minioProperties.getBucketUserAvatar();
        this.userServiceFeignClient = userServiceFeignClient;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public String saveAvatar(long userId, ImageRequestDTO image) {
        log.info("Executing saveImage for user: {}", userId);

        log.info("Saving image for user: {}", userId);
        String newImageId = minioService.upload(
                image.file(),
                bucketName
        );

        log.info("Send data userId: {}, imageId: {} in kafka for saveImageIdEvent",
                userId,
                newImageId
        );
        kafkaProducerService.sendSaveImageIdEvent(userId, newImageId);

        return newImageId;
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

    @Override
    public void deleteAvatar(long userId) {
        log.info("Executing deleteAvatar for user: {}", userId);

        log.info("Retrieving imageId for user: {} from user-service", userId);
        String imageId = userServiceFeignClient.deleteImageIdByUserId(userId).getBody();

        log.info("Deleting image: {}", imageId);
        minioService.deleteImage(imageId, bucketName);
    }

    @Override
    public void deleteImage(String imageId) {
        log.info("Executing deleteImage for imageId: {}", imageId);

        log.info("Deleting image: {}", imageId);
        minioService.deleteImage(imageId, bucketName);
    }
}
