package ru.andreyszdlv.postservice.dto.kafkaDto;

public record CreateCommentKafkaDTO(
        String email,
        String nameCommentAuthor,
        String content
) {
}
