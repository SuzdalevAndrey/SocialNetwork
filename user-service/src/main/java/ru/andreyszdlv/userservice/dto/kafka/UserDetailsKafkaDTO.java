package ru.andreyszdlv.userservice.dto.kafka;

import lombok.Builder;
import ru.andreyszdlv.userservice.enums.ERole;

@Builder
public record UserDetailsKafkaDTO(
        String name,

        String email,

        String password,

        ERole role
) {
}
