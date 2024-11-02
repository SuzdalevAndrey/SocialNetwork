package ru.andreyszdlv.userservice.dto.kafka;

public record SaveImageIdKafkaDTO(
        long userId,
        String imageId
) {
}
