package ru.andreyszdlv.authservice.dto.kafkadto;

public record RegisterUserKafkaDTO (
        String email,

        String code
){ }
