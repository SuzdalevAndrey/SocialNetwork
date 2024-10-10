package ru.andreyszdlv.authservice.dto.userservicefeigndto;

import ru.andreyszdlv.authservice.enums.ERole;

public record UserDetailsResponseDTO(
        Long id,

        String name,

        String email,

        String password,

        ERole role
) {
}
