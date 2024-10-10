package ru.andreyszdlv.authservice.dto.userservicefeigndto;

import lombok.Builder;
import ru.andreyszdlv.authservice.enums.ERole;

@Builder
public record UserDetailsRequestDTO(
        Long id,

        String name,

        String email,

        String password,

        ERole role
) {
}
