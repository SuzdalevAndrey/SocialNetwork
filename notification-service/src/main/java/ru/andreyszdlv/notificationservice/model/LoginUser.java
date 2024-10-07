package ru.andreyszdlv.notificationservice.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Data
public class LoginUser{
    private String name;

    private String email;

}
