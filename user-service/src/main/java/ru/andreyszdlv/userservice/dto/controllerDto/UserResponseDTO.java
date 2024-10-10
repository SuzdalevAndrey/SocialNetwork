package ru.andreyszdlv.userservice.dto.controllerDto;

import lombok.Builder;
import ru.andreyszdlv.userservice.security.enums.ERole;

@Builder
public record UserResponseDTO (
        String name,
        String email,
        ERole role
){ }
