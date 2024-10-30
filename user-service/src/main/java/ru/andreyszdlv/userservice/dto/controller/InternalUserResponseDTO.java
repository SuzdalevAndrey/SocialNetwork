package ru.andreyszdlv.userservice.dto.controller;

import lombok.Builder;
import ru.andreyszdlv.userservice.enums.ERole;

@Builder
public record InternalUserResponseDTO(
        long id,
        String name,
        String email,
        ERole role
){ }
