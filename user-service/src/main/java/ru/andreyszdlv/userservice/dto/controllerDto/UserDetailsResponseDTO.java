package ru.andreyszdlv.userservice.dto.controllerDto;

import lombok.Builder;
import ru.andreyszdlv.userservice.security.enums.ERole;

@Builder
public record UserDetailsResponseDTO(
        Long id,

        String name,

        String email,

        String password,

        ERole role
) {
}
