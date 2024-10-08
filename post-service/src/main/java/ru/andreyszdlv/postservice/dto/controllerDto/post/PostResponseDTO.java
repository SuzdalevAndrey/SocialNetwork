package ru.andreyszdlv.postservice.dto.controllerDto.post;

import ru.andreyszdlv.postservice.model.Like;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;

public record PostResponseDTO(
        Post post,

        List<Like> likes
) {
}
