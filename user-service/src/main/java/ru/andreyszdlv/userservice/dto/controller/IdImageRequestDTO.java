package ru.andreyszdlv.userservice.dto.controller;

import org.hibernate.validator.constraints.NotBlank;

public record IdImageRequestDTO(
        @NotBlank(message = "")
        String idImage
) {
}
