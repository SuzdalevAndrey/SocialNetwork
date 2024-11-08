package ru.andreyszdlv.authservice.dto.kafka;

import lombok.Builder;
import ru.andreyszdlv.authservice.enums.ERole;

import java.util.UUID;

@Builder
public record UserDetailsKafkaDTO(
        UUID messageId,

        String name,

        String email,

        String password,

        ERole role
) {
}
