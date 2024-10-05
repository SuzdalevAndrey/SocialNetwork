package ru.andreyszdlv.authservice.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserModel {
    private String name;

    private String email;
}
