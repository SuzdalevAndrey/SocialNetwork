package ru.andreyszdlv.postservice.dto.kafka;

public record CreateCommentKafkaDTO(
        String email,
        String nameCommentAuthor,
        String content
) {
}
