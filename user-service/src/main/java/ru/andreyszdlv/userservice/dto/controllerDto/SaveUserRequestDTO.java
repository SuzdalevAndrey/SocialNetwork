package ru.andreyszdlv.userservice.dto.controllerDto;

import ru.andreyszdlv.userservice.security.enums.ERole;

public record SaveUserRequestDTO(
        String name,

        String email,

        String password,

        ERole role
) {
}
