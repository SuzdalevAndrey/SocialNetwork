package ru.andreyszdlv.userservice.dto.kafka;

public record EditEmailKafkaDTO(
        String oldEmail,
        String newEmail
) {
}
