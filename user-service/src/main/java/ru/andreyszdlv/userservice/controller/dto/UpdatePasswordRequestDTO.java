package ru.andreyszdlv.userservice.controller.dto;

public record UpdatePasswordRequestDTO(
        String oldPassword,
        String newPassword
) {
}
