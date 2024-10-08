package ru.andreyszdlv.postservice.dto.kafkaDto;

public record CreateLikeKafkaDTO(
        String email,
        String nameLikeAuthor
) {
}
