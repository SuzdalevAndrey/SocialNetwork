package ru.andreyszdlv.postservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.andreyszdlv.postservice.dto.controller.post.PostImageIdsResponseDTO;
import ru.andreyszdlv.postservice.dto.controller.post.PostResponseDTO;
import ru.andreyszdlv.postservice.model.Post;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    PostResponseDTO postToPostResponseDTO(Post post);

    List<PostResponseDTO> listPostToListPostResponseDTO(List<Post> posts);

    PostImageIdsResponseDTO postToPostImageIdsResponseDTO(Post post);
}
