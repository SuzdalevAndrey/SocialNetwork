package ru.andreyszdlv.postservice.dto.controller.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdatePostRequestDTO(
        @NotBlank(message = "{error.post.content.is_empty}")
        @Size(min = 1, max = 1000, message = "{error.post.content.is_not_valid}")
        String content,

        @Size(max = 10, message = "{error.post.image.is_not_valid}")
        List<MultipartFile> images
) {
}
