package ru.andreyszdlv.userservice.dto.controller;

import lombok.Builder;
import ru.andreyszdlv.userservice.enums.ERole;

@Builder
public record UserDetailsResponseDTO(
        Long id,

        String name,

        String email,

        String password,

        ERole role
) {
}
