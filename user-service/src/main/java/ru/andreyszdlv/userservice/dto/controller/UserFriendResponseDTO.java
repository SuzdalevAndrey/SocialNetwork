package ru.andreyszdlv.userservice.dto.controller;

import lombok.Builder;

@Builder
public record UserFriendResponseDTO(
        String name,
        String email
)
{ }
