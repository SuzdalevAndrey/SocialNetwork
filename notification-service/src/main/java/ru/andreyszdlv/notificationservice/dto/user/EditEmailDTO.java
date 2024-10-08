package ru.andreyszdlv.notificationservice.dto.user;

public record EditEmailDTO(
        String oldEmail,
        String newEmail
) {
}
