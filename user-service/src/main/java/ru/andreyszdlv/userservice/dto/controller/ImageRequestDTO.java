package ru.andreyszdlv.userservice.dto.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ImageRequestDTO(

        @NotNull(message = "{data.user.image.is_empty}")
        MultipartFile image
) {
}
