package ru.andreyszdlv.postservice.dto.controller.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record AddImagePostRequestDTO(
        @NotNull(message = "{error.post.image.is_not_valid}")
        @Size(min = 1, max = 10, message = "{error.post.image.is_not_valid}")
        List<MultipartFile> images
) {
}
