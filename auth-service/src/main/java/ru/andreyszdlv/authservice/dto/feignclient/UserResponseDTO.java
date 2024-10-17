package ru.andreyszdlv.authservice.dto.feignclient;

import ru.andreyszdlv.authservice.enums.ERole;

public record UserResponseDTO(
        String name,
        String email,
        ERole role
) {
}
