package ru.andreyszdlv.authservice.dto.kafkadto;

public record LoginUserKafkaDTO (
        String name,

        String email
){ }
