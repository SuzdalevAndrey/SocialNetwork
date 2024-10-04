package ru.andreyszdlv.notificationservice.model;

import lombok.Data;

@Data
public class UserModel {
    private String action;

    private String name;

    private String email;
}
