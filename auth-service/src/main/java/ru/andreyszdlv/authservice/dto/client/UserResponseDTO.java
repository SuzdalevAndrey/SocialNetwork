package ru.andreyszdlv.authservice.dto.client;

import ru.andreyszdlv.authservice.enums.ERole;

public record UserResponseDTO(
        long id,
        String name,
        String email,
        ERole role
) {
}
