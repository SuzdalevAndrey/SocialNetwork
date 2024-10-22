package ru.andreyszdlv.postservice.mapper;

import org.springframework.stereotype.Component;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;

public interface PostMapper {

    PostResponseDTO postToPostResponseDTO(Post post);

    List<PostResponseDTO> listPostToListPostResponseDTO(List<Post> posts);
}
