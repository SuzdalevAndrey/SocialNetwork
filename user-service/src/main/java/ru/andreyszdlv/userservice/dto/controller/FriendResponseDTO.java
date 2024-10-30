package ru.andreyszdlv.userservice.dto.controller;

import lombok.Builder;

@Builder
public record FriendResponseDTO(
        String name,
        String idImage
)
{ }
