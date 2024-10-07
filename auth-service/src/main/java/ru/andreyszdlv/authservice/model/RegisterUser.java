package ru.andreyszdlv.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUser {

    private String email;

    private String code;
}
