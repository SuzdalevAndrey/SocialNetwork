package ru.andreyszdlv.authservice.dto.kafkadto;

import lombok.Builder;
import ru.andreyszdlv.authservice.enums.ERole;

@Builder
public record UserDetailsKafkaDTO(
        Long id,

        String name,

        String email,

        String password,

        ERole role
) {
}
