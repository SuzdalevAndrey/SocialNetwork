package ru.andreyszdlv.postservice.mapper;

import org.springframework.stereotype.Component;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.model.Post;

@Component
public interface PostMapper {

    PostResponseDTO postToPostResponseDTO(Post post);
}
