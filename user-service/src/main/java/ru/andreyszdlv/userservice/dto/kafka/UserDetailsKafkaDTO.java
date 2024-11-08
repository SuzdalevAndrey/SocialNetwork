package ru.andreyszdlv.userservice.dto.kafka;

import lombok.Builder;
import ru.andreyszdlv.userservice.enums.ERole;

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
