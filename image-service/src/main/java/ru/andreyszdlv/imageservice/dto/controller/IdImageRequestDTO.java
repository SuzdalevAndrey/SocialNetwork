package ru.andreyszdlv.imageservice.dto.controller;

import jakarta.validation.constraints.NotBlank;

public record IdImageRequestDTO(
        @NotBlank
        String idImage
) {
}
