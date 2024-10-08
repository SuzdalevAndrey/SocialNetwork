package ru.andreyszdlv.userservice.dto.kafkaDto;

public record EditEmailKafkaDTO(
        String oldEmail,
        String newEmail
) {
}
