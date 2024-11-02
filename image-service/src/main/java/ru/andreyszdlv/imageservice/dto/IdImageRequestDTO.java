package ru.andreyszdlv.imageservice.dto;

import jakarta.validation.constraints.NotBlank;

public record IdImageRequestDTO(
        @NotBlank
        String idImage
) {
}
