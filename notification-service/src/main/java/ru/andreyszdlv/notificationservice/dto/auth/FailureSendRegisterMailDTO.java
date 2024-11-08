package ru.andreyszdlv.notificationservice.dto.auth;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FailureSendRegisterMailDTO(
        UUID messageId,
        String email
){
}
