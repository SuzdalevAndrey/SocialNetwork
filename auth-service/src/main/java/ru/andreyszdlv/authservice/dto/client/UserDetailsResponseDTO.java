package ru.andreyszdlv.authservice.dto.client;

import ru.andreyszdlv.authservice.enums.ERole;

public record UserDetailsResponseDTO(
        Long id,

        String name,

        String email,

        String password,

        ERole role
) {
}
