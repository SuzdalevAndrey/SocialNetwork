package ru.andreyszdlv.postservice.dto.controller.post;

import java.util.List;

public record PostImageIdsResponseDTO(
        List<String> imageIds
){
}
