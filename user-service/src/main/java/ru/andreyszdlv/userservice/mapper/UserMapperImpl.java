package ru.andreyszdlv.userservice.mapper;

import org.springframework.stereotype.Component;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.model.User;

@Component
public class UserMapperImpl implements UserMapper{
    @Override
    public UserResponseDTO userToUserResponseDTO(User user) {
        if(user == null)
            return null;
        return UserResponseDTO
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserDetailsResponseDTO userToUserDetailsResponseDTO(User user) {
        if(user == null)
            return null;
        return UserDetailsResponseDTO
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
