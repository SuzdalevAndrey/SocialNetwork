package ru.andreyszdlv.imageservice.dto.kafka;

import lombok.Builder;

@Builder
public record SaveImageIdKafkaDTO(
        long userId,
        String imageId
) {
}
