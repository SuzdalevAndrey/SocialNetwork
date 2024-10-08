package ru.andreyszdlv.notificationservice.dto.user;

public record EditEmailKafkaDTO(
        String oldEmail,
        String newEmail
) {
}
