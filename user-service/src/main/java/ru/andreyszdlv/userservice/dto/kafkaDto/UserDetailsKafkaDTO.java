package ru.andreyszdlv.userservice.dto.kafkaDto;

import lombok.Builder;
import ru.andreyszdlv.userservice.security.enums.ERole;

@Builder
public record UserDetailsKafkaDTO(
        String name,

        String email,

        String password,

        ERole role
) {
}
