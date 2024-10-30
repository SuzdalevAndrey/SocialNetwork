package ru.andreyszdlv.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.andreyszdlv.userservice.dto.controller.InternalUserResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponseDTO userToUserResponseDTO(User user);

    UserDetailsResponseDTO userToUserDetailsResponseDTO(User user);

    InternalUserResponseDTO userToInternalUserResponseDTO(User user);
}
