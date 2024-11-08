package ru.andreyszdlv.authservice.dto.kafka;

import java.util.UUID;

public record FailureSendRegisterMailKafkaDTO(
        UUID messageId,
        String email
){ }
