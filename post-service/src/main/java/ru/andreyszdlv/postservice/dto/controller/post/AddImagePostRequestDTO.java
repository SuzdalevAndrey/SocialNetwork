package ru.andreyszdlv.postservice.dto.controller.post;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record AddImagePostRequestDTO(
        List<MultipartFile> images
) {
}
