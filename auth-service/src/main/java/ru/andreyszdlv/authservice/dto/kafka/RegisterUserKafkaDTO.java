package ru.andreyszdlv.authservice.dto.kafka;

public record RegisterUserKafkaDTO (
        String email,

        String code
){ }
