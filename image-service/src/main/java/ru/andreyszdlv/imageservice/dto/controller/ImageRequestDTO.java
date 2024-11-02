package ru.andreyszdlv.imageservice.dto.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ImageRequestDTO (

        @NotNull(message = "")
        MultipartFile file
){
}
