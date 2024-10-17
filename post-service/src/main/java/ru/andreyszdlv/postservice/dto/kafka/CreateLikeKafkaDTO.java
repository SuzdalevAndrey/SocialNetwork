package ru.andreyszdlv.postservice.dto.kafka;

public record CreateLikeKafkaDTO(
        String email,
        String nameLikeAuthor
) {
}
