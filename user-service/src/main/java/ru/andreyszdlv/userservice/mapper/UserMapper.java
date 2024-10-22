package ru.andreyszdlv.userservice.mapper;

import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.model.User;

public interface UserMapper {

    UserResponseDTO userToUserResponseDTO(User user);

    UserDetailsResponseDTO userToUserDetailsResponseDTO(User user);
}
